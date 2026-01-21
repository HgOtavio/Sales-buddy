const { Sale, SaleItem } = require('../models');

exports.getItemsBySaleId = async (saleId) => {
    // Verifica se a venda existe antes de buscar os itens
    const saleExists = await Sale.findByPk(saleId);
    if (!saleExists) {
        throw { status: 404, message: "Venda não encontrada." };
    }

    // Busca os itens vinculados a essa venda
    return await SaleItem.findAll({ 
        where: { saleId },
        attributes: ['id', 'productName', 'quantity', 'unitPrice', 'totalItemPrice']
    });
};

exports.addItemToSale = async (saleId, itemData) => {
    const sale = await Sale.findByPk(saleId);
    if (!sale) {
        throw { status: 404, message: "Venda não encontrada para adicionar item." };
    }

    const quantity = itemData.quantity || 1;
    const price = parseFloat(itemData.price || 0);

    const newItem = await SaleItem.create({
        saleId: sale.id,
        productName: itemData.name, 
        unitPrice: price,
        quantity: quantity,
        totalItemPrice: price * quantity
    });

    return newItem;
};