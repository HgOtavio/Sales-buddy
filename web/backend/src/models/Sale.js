const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig'); // Ajuste o caminho se necessário
const Sale = sequelize.define('Sale', {
    clientName: { 
        type: DataTypes.STRING, 
        allowNull: false 
    },
    clientCpf: { 
        type: DataTypes.STRING 
    },
    clientEmail: { 
        type: DataTypes.STRING 
    },
    saleValue: { 
        type: DataTypes.DECIMAL(10, 2), 
        allowNull: false 
    },
    receivedValue: { 
        type: DataTypes.DECIMAL(10, 2), 
        allowNull: false 
    },
    change: { // O Troco
        type: DataTypes.DECIMAL(10, 2), 
        allowNull: false 
    }
});

module.exports = Sale;