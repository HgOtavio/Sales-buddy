const { Item, Sale } = require('../models');

exports.getItemsBySaleId = async (saleId) => {
    const exists = await Sale.findByPk(saleId);
    if (!exists) throw { status: 404, message: "Sale not found" };

    return await Item.findAll({ where: { SaleId: saleId } });
};

exports.addItemToSale = async (saleId, itemData) => {
    const sale = await Sale.findByPk(saleId);
    if (!sale) throw { status: 404, message: "Sale not found to add item" };

    const newItem = await Item.create({
        name: itemData.name,
        price: itemData.price || 0,
        SaleId: saleId
    });

    
    return newItem;
};