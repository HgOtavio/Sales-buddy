const { sequelize } = require('../config/dbConfig');

const Company = require('./Company');
const User = require('./User');
const Sale = require('./Sale');
const SaleItem = require('./SaleItem'); // Certifique-se que o arquivo é SaleItem.js

// --- DEFINIÇÃO DE RELACIONAMENTOS ---

// 1. Usuário e Empresa
Company.hasMany(User, { foreignKey: 'companyId' });
User.belongsTo(Company, { foreignKey: 'companyId' });

// 2. Usuário e Vendas
User.hasMany(Sale, { foreignKey: 'userId' });
Sale.belongsTo(User, { foreignKey: 'userId' });

// 3. Venda e Itens (Onde deu o erro)
// O alias 'saleItems' é importante para o 'include' funcionar no Service
Sale.hasMany(SaleItem, { as: 'saleItems', foreignKey: 'saleId', onDelete: 'CASCADE' });
SaleItem.belongsTo(Sale, { foreignKey: 'saleId' });

module.exports = { 
    sequelize,
    Company, 
    User, 
    Sale, 
    SaleItem 
};