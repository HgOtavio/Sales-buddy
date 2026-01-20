const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig');

const Reprocessing = sequelize.define('Reprocessing', {
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
    change: { 
        type: DataTypes.DECIMAL(10, 2), 
        allowNull: false 
    },
    // Campo extra opcional para saber o motivo do erro
    errorReason: {
        type: DataTypes.STRING,
        allowNull: true
    }
});

module.exports = Reprocessing;