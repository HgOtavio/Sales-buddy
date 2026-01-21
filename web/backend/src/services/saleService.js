const { sequelize } = require('../config/dbConfig');
const { Sale, SaleItem } = require('../models');

exports.createNewSale = async (data) => {
    const transaction = await sequelize.transaction();

    try {
        const { 
            userId, clientName, clientCpf, clientEmail, 
            saleValue, receivedValue, items 
        } = data;

        // Garante que são números para o cálculo
        const vSale = parseFloat(saleValue);
        const vReceived = parseFloat(receivedValue);

        // Validação de Negócio: Pagamento insuficiente
        if (vReceived < vSale) {
            throw { 
                status: 402, // Payment Required
                message: "Valor recebido insuficiente.", 
                missing: (vSale - vReceived).toFixed(2) 
            };
        }

        const calculatedChange = vReceived - vSale;

        // 1. Cria a Venda
        const newSale = await Sale.create({
            userId,
            clientName,
            clientCpf,
            clientEmail,
            saleValue: vSale,
            receivedValue: vReceived,
            change: calculatedChange
        }, { transaction });

        // 2. Cria os Itens da Venda
        if (items && Array.isArray(items) && items.length > 0) {
            const itemList = items.map(item => {
                const qtd = item.quantity || 1;
                const price = item.price || 0; // O JSON usa 'price', o banco usa 'unitPrice'

                return {
                    saleId: newSale.id,
                    // Aceita 'productName' (do JSON novo) ou 'name' (legado) ou o próprio item se for string
                    productName: item.productName || item.name || (typeof item === 'string' ? item : "Produto sem nome"),
                    quantity: qtd,
                    unitPrice: price,
                    totalItemPrice: price * qtd
                };
            });

            await SaleItem.bulkCreate(itemList, { transaction });
        }

        await transaction.commit();

        return { 
            saleId: newSale.id, 
            change: calculatedChange, 
            status: "success",
            message: "Venda registrada com sucesso!"
        };

    } catch (error) {
        await transaction.rollback();
        throw error;
    }
};

exports.getSalesByUser = async (userId) => {
    if (!userId) throw { status: 400, message: "User ID is required" };

    return await Sale.findAll({
        where: { userId },
        include: [{ 
            model: SaleItem, 
            as: 'saleItems',
            attributes: ['productName', 'unitPrice', 'quantity', 'totalItemPrice']
        }],
        order: [['createdAt', 'DESC']]
    });
};

exports.getSaleById = async (saleId) => {
    const sale = await Sale.findByPk(saleId, {
        include: [{ model: SaleItem, as: 'saleItems' }]
    });
    
    if (!sale) throw { status: 404, message: "Venda não encontrada." };
    
    return sale;
};