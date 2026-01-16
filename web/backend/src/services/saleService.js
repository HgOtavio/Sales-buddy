const { sequelize } = require('../config/dbConfig');
const { Sale, SaleItem } = require('../models');

exports.createNewSale = async (data) => {
    const transaction = await sequelize.transaction();

    try {
        const { 
            userId, clientName, clientCpf, clientEmail, 
            saleValue, receivedValue, items 
        } = data;

        const vSale = parseFloat(saleValue);
        const vReceived = parseFloat(receivedValue);

        if (vReceived < vSale) {
            throw { 
                status: 402, 
                message: "Valor recebido insuficiente.", 
                missing: (vSale - vReceived).toFixed(2) 
            };
        }

        const calculatedChange = vReceived - vSale;

        const newSale = await Sale.create({
            userId,
            clientName,
            clientCpf,
            clientEmail,
            saleValue: vSale,
            receivedValue: vReceived,
            change: calculatedChange
        }, { transaction });

        if (items && Array.isArray(items) && items.length > 0) {
            const itemList = items.map(item => ({
                productName: typeof item === 'string' ? item : item.name,
                unitPrice: item.price || 0,
                quantity: item.quantity || 1,
                totalItemPrice: (item.price || 0) * (item.quantity || 1),
                saleId: newSale.id
            }));

            await SaleItem.bulkCreate(itemList, { transaction });
        }

        await transaction.commit();

        return { 
            saleId: newSale.id, 
            change: calculatedChange, 
            status: "success" 
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
            attributes: ['productName', 'unitPrice', 'quantity']
        }],
        order: [['createdAt', 'DESC']]
    });
};

exports.getSaleById = async (saleId) => {
    const sale = await Sale.findByPk(saleId, {
        include: [{ model: SaleItem, as: 'saleItems' }]
    });
    if (!sale) throw { status: 404, message: "Sale not found" };
    return sale;
};