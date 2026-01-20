const { sequelize } = require('../config/dbConfig');

const Company = require('./Company');
const User = require('./User');
const Sale = require('./Sale');
const SaleItem = require('./SaleItem'); 
// Importar os novos models
const Reprocessing = require('./Reprocessing');
const ReprocessingItem = require('./ReprocessingItem');

// 1. Usuário e Empresa
Company.hasMany(User, { foreignKey: 'companyId' });
User.belongsTo(Company, { foreignKey: 'companyId' });

// 2. Usuário e Vendas Oficiais
User.hasMany(Sale, { foreignKey: 'userId' });
Sale.belongsTo(User, { foreignKey: 'userId' });

// 2.1. Usuário e Reprocessamento (PENDÊNCIAS)
User.hasMany(Reprocessing, { foreignKey: 'userId', as: 'pendingSales' });
Reprocessing.belongsTo(User, { foreignKey: 'userId' });

// 3. Venda e Itens 
Sale.hasMany(SaleItem, { as: 'saleItems', foreignKey: 'saleId', onDelete: 'CASCADE' });
SaleItem.belongsTo(Sale, { foreignKey: 'saleId' });

// 3.1. Reprocessamento e Seus Itens
Reprocessing.hasMany(ReprocessingItem, { as: 'reprocessingItems', foreignKey: 'reprocessingId', onDelete: 'CASCADE' });
ReprocessingItem.belongsTo(Reprocessing, { foreignKey: 'reprocessingId' });

module.exports = { 
    sequelize,
    Company, 
    User, 
    Sale, 
    SaleItem,
    Reprocessing,
    ReprocessingItem
};