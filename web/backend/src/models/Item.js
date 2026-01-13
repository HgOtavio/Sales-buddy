const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig'); // Ajuste o caminho se necessário
const Item = sequelize.define('Item', {
    name: { 
        type: DataTypes.STRING, 
        allowNull: false 
    },
    price: { 
        type: DataTypes.DECIMAL(10, 2), 
        defaultValue: 0.00 
    }
});

module.exports = Item;