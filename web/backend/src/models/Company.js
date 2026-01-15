// models/Company.js
const { DataTypes } = require('sequelize');
const { sequelize } = require('../config/dbConfig'); // Ajuste o caminho se necessário

const Company = sequelize.define('Company', {
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
  },
  name: {
    type: DataTypes.STRING,
    allowNull: false,
  },
  taxId: { 
    type: DataTypes.STRING,
    allowNull: false,
    unique: true // Garante que só existe um registro por CNPJ
  },
  
  ownerId: {
    type: DataTypes.INTEGER,
    allowNull: true
  }
});

module.exports = Company;