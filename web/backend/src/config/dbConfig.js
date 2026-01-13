const { Sequelize } = require('sequelize');
require('dotenv').config();

const sequelize = new Sequelize(
    process.env.DB_NAME,
    process.env.DB_USER,
    process.env.DB_PASS,
    {
        host: process.env.DB_HOST,
        dialect: 'postgres',
        logging: false,
    }
);

const conectarBanco = async () => {
    try {
        await sequelize.authenticate();
        await sequelize.sync({ alter: true });
        console.log('Conexão com PostgreSQL estabelecida com sucesso.');
    } catch (error) {
        console.error(' Não foi possível conectar ao banco de dados:', error);
        process.exit(1);
    }
};

module.exports = { sequelize, conectarBanco };