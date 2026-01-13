const { sequelize } = require('../config/dbConfig'); // Ajuste o caminho se necessário
const User = require('./User');
const Sale = require('./Sale');
const Item = require('./Item');


User.hasMany(Sale);
Sale.belongsTo(User);


Sale.hasMany(Item, { as: 'saleItems', onDelete: 'CASCADE' }); 
Item.belongsTo(Sale);

const connectDatabase = async () => {
    try {
        await sequelize.authenticate();
        await sequelize.sync({ alter: true }); 
        console.log("Database connected and models synchronized.");
    } catch (error) {
        console.error(" Connection error:", error);
    }
};

module.exports = { 
    sequelize, 
    connectDatabase, 
    User, 
    Sale, 
    Item 
};