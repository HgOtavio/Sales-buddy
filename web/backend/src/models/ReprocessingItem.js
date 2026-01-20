const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig');

const ReprocessingItem = sequelize.define('ReprocessingItem', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true
    },
    productName: { 
        type: DataTypes.STRING,
        allowNull: false
    },
    quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 1
    },
    unitPrice: { 
        type: DataTypes.DECIMAL(10, 2),
        allowNull: false
    },
    totalItemPrice: { 
        type: DataTypes.DECIMAL(10, 2),
        allowNull: true
    }
});

module.exports = ReprocessingItem;