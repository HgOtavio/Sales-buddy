const { sequelize, Reprocessing, ReprocessingItem, Sale, SaleItem } = require('../models');

module.exports = {
    
    // 1. Criar um item para reprocessamento (Salvar rascunho/erro)
    async create(req, res) {
        try {
            const { userId, clientName, saleValue, receivedValue, change, items, errorReason } = req.body;

            const newRepo = await Reprocessing.create({
                userId,
                clientName,
                saleValue,
                receivedValue,
                change,
                errorReason
            });

            // Adiciona os itens
            if (items && items.length > 0) {
                const itemsData = items.map(item => ({
                    ...item,
                    reprocessingId: newRepo.id
                }));
                await ReprocessingItem.bulkCreate(itemsData);
            }

            return res.status(201).json(newRepo);
        } catch (error) {
            return res.status(500).json({ error: error.message });
        }
    },

    // 2. Listar o que está pendente
    async list(req, res) {
        try {
            const { userId } = req.params; // Ou pegar do token
            const list = await Reprocessing.findAll({
                where: { userId },
                include: [{ model: ReprocessingItem, as: 'reprocessingItems' }]
            });
            return res.json(list);
        } catch (error) {
            return res.status(500).json({ error: error.message });
        }
    },

    async approveAndMoveToSale(req, res) {
        const { id } = req.params; // ID do Reprocessamento
        const transaction = await sequelize.transaction(); // Inicia transação segura

        try {
            // A. Buscar o registro no Reprocessamento com os itens
            const pendingData = await Reprocessing.findByPk(id, {
                include: [{ model: ReprocessingItem, as: 'reprocessingItems' }],
                transaction
            });

            if (!pendingData) {
                await transaction.rollback();
                return res.status(404).json({ error: "Item não encontrado." });
            }

            // B. Criar a Venda Oficial (Sale) copiando os dados
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
                    saleId: newSale.id, // ID da nova venda
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
            console.error(error);
            return res.status(500).json({ error: "Erro ao processar venda." });
        }
    }
};