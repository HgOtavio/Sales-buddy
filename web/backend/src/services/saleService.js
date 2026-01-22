const { sequelize } = require('../config/dbConfig');
const { Sale, SaleItem } = require('../models');

exports.createNewSale = async (data) => {
    const transaction = await sequelize.transaction();

    try {
        // 1. Sanitização dos dados de entrada (Evita NULL e UNDEFINED)
        // Se não vier nome, define como "Consumidor Final". Se não vier CPF/Email, define como string vazia.
        const userId = data.userId; // Esse é obrigatório, deve vir validado do controller
        const clientName = data.clientName || "Consumidor Final"; 
        const clientCpf = data.clientCpf || ""; 
        const clientEmail = data.clientEmail || "";
        
        // Garante que são números, prevenindo NaN
        const vSale = parseFloat(data.saleValue) || 0;
        const vReceived = parseFloat(data.receivedValue) || 0;
        const items = data.items || [];

        // 2. Validações de Regra de Negócio
        if (vReceived < vSale) {
            throw { 
                status: 402, 
                message: "Valor recebido insuficiente.", 
                missing: (vSale - vReceived).toFixed(2) 
            };
        }

        if (!items || items.length === 0) {
            throw { status: 400, message: "A venda deve conter pelo menos um item." };
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
            const itemList = items.map(item => {
                const qtd = parseFloat(item.quantity) || 1;
                const price = parseFloat(item.price || item.unitPrice) || 0; 
                
                let prodName = item.productName || item.name;
                if (!prodName && typeof item === 'string') prodName = item;
                if (!prodName) prodName = "Produto Genérico"; 

                return {
                    saleId: newSale.id,
                    productName: prodName,
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
        if (transaction) await transaction.rollback();
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