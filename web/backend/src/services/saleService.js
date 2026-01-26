const { sequelize } = require('../config/dbConfig');
const { Sale, SaleItem } = require('../models');
const { isValidCPF, isValidEmail } = require('../utils/userHelpers'); 

exports.createNewSale = async (data) => {
    const transaction = await sequelize.transaction();

    try {
        const requiredFields = ['userId', 'clientName', 'clientCpf', 'clientEmail', 'saleValue', 'receivedValue', 'items'];
        for (const field of requiredFields) {
            const value = typeof data[field] === 'string' ? data[field].trim() : data[field];
            
            if (value === undefined || value === null || value === "") {
                throw { status: 400, message: `O campo '${field}' é obrigatório e não pode ficar em branco.` };
            }
        }

        if (!isValidEmail(data.clientEmail.trim())) {
            throw { status: 400, message: "E-mail inválido. Verifique se o domínio está correto (ex: .com ou .com.br)." };
        }

        if (!isValidCPF(data.clientCpf)) {
            throw { status: 400, message: "CPF inválido. Verifique os números digitados." };
        }

        const vSale = parseFloat(data.saleValue);
        const vReceived = parseFloat(data.receivedValue);

        if (isNaN(vSale) || isNaN(vReceived)) {
            throw { status: 400, message: "Os campos 'saleValue' e 'receivedValue' devem ser números válidos." };
        }

        if (vReceived < vSale) {
            throw { 
                status: 402, 
                message: "Valor recebido insuficiente.", 
                missing: (vSale - vReceived).toFixed(2) 
            };
        }

        if (!Array.isArray(data.items) || data.items.length === 0) {
            throw { status: 400, message: "A venda deve conter uma lista com pelo menos um item." };
        }

        const calculatedChange = vReceived - vSale;

        const newSale = await Sale.create({
            userId: data.userId,
            clientName: data.clientName.trim(),  
            clientCpf: data.clientCpf.replace(/[^\d]+/g, ''), 
            clientEmail: data.clientEmail.trim().toLowerCase(), 
            saleValue: vSale,
            receivedValue: vReceived,
            change: calculatedChange
        }, { transaction });

        const itemList = data.items.map((item, index) => {
            const prodName = item.productName || item.name; 
            const qtd = parseFloat(item.quantity);
            const price = parseFloat(item.unitPrice || item.price); 

            if (!prodName || typeof prodName !== 'string' || prodName.trim() === "") {
                throw { status: 400, message: `Nome do produto ausente ou em branco na posição ${index + 1}.` };
            }
            if (isNaN(qtd) || qtd <= 0) throw { status: 400, message: `Quantidade inválida no item '${prodName}'. Deve ser maior que zero.` };
            if (isNaN(price) || price < 0) throw { status: 400, message: `Preço inválido no item '${prodName}'. Não pode ser negativo.` };

            return {
                saleId: newSale.id,
                productName: prodName.trim(),
                quantity: qtd,
                unitPrice: price,
                totalItemPrice: price * qtd
            };
        });

        await SaleItem.bulkCreate(itemList, { transaction });

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

exports.getAllSales = async () => {
    return await Sale.findAll({
        include: [{ 
            model: SaleItem, 
            as: 'saleItems',
            attributes: ['productName', 'unitPrice', 'quantity', 'totalItemPrice']
        }],
        order: [['createdAt', 'DESC']]
    });
};