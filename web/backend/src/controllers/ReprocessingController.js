const { sequelize, Reprocessing, ReprocessingItem, Sale, SaleItem } = require('../models');

// Importa os helpers padronizados
const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

module.exports = {
    
    // 1. Criar um item para reprocessamento (Salvar rascunho/erro)
    async create(req, res) {
        try {
            const errorMsg = validateRequiredFields(req.body, ['clientName', 'saleValue']);
            if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

            const { userId, clientName, saleValue, receivedValue, change, items, errorReason } = req.body;

            // Usa o ID do token se disponível, senão pega do body
            const finalUserId = req.user ? req.user.id : userId;

            if (!finalUserId) {
                return res.status(400).json({ error: "User ID necessário." });
            }

            const newRepo = await Reprocessing.create({
                userId: finalUserId,
                clientName,
                saleValue,
                receivedValue,
                change,
                errorReason
            });

            if (items && items.length > 0) {
                const itemsData = items.map(item => ({
                    ...item,
                    reprocessingId: newRepo.id
                }));
                await ReprocessingItem.bulkCreate(itemsData);
            }

            return res.status(201).json(newRepo);
        } catch (error) {
            handleError(res, error);
        }
    },

    // 2. Listar o que está pendente
    async list(req, res) {
        try {
            // Lógica: Se o usuário estiver logado, lista os dele.
            // Se for uma chamada administrativa (opcional), pode passar userId no body.
            const userId = req.user ? req.user.id : req.body.userId;
            
            if (!userId) {
                return res.status(400).json({ error: "User ID necessário para listar." });
            }

            const list = await Reprocessing.findAll({
                where: { userId },
                include: [{ model: ReprocessingItem, as: 'reprocessingItems' }]
            });
            return res.json(list);
        } catch (error) {
            handleError(res, error);
        }
    },

    // 3. Aprovar e Mover para Venda
    async approveAndMoveToSale(req, res) {
        // MUDANÇA: ID vem do Body agora
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "ID obrigatório", message: errorMsg });

        const { id } = req.body; 
        const transaction = await sequelize.transaction(); 

        try {
            const pendingData = await Reprocessing.findByPk(id, {
                include: [{ model: ReprocessingItem, as: 'reprocessingItems' }],
                transaction
            });

            if (!pendingData) {
                await transaction.rollback();
                return res.status(404).json({ error: "Não Encontrado", message: "Item de reprocessamento não existe." });
            }

            const newSale = await Sale.create({
                userId: pendingData.userId,
                clientName: pendingData.clientName,
                clientCpf: pendingData.clientCpf,
                clientEmail: pendingData.clientEmail,
                saleValue: pendingData.saleValue,
                receivedValue: pendingData.receivedValue,
                change: pendingData.change
            }, { transaction });

            if (pendingData.reprocessingItems && pendingData.reprocessingItems.length > 0) {
                const itemsToMove = pendingData.reprocessingItems.map(item => ({
                    saleId: newSale.id, 
                    productName: item.productName,
                    quantity: item.quantity,
                    unitPrice: item.unitPrice,
                    totalItemPrice: item.totalItemPrice
                }));

                await SaleItem.bulkCreate(itemsToMove, { transaction });
            }

            await pendingData.destroy({ transaction });

            await transaction.commit();

            return res.status(200).json({ message: "Venda processada com sucesso!", sale: newSale });

        } catch (error) {
            await transaction.rollback();
            console.error("Erro na transação:", error);
            return res.status(500).json({ error: "Erro ao processar venda.", message: error.message });
        }
    }
};