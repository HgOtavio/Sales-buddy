const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig');

const SaleItem = sequelize.define('SaleItem', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    productName: { // Nome do produto no momento da venda
        type: DataTypes.STRING,
        allowNull: false
    },
    quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 1
    },
    unitPrice: { // Preço unitário
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false
    },
    totalItemPrice: { // (Qtd * Unitário) - Para facilitar relatórios futuros
        type: DataTypes.DECIMAL(10, 2),
        allowNull: true
    }
    // O saleId será criado automaticamente pela associação no dbConfig
});

module.exports = SaleItem;