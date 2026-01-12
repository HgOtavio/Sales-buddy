const { Sequelize, DataTypes } = require('sequelize');

// Conexão usando as variáveis do .env
const sequelize = new Sequelize(
    process.env.DB_NAME,     // salesbuddy_db
    process.env.DB_USER,     // admin
    process.env.DB_PASS,     // password123
    {
        host: process.env.DB_HOST,       // localhost
        dialect: process.env.DB_DIALECT, // postgres
        logging: false
    }
);

// Modelo USUÁRIO
const Usuario = sequelize.define('Usuario', {
    nome: { type: DataTypes.STRING, allowNull: false },
    empresa: { type: DataTypes.STRING, allowNull: false },
    email: { type: DataTypes.STRING, allowNull: false, unique: true },
    cnpj: { type: DataTypes.STRING, allowNull: false },
    senha: { type: DataTypes.STRING, allowNull: false }
});

//  Modelo VENDA
const Venda = sequelize.define('Venda', {
    nome_cliente: { type: DataTypes.STRING, allowNull: false },
    cpf_cliente: { type: DataTypes.STRING },
    email_cliente: { type: DataTypes.STRING },
    valor_venda: { type: DataTypes.DECIMAL(10, 2), allowNull: false },
    valor_recebido: { type: DataTypes.DECIMAL(10, 2), allowNull: false }
});

//  Modelo ITEM
const Item = sequelize.define('Item', {
    nome: { type: DataTypes.STRING, allowNull: false },
    valor: { type: DataTypes.DECIMAL(10, 2), allowNull: false }
});

//  RELACIONAMENTOS
Usuario.hasMany(Venda);
Venda.belongsTo(Usuario);

Venda.hasMany(Item);
Item.belongsTo(Venda);

//  Conexão
const conectarBanco = async () => {
    try {
        await sequelize.authenticate();
        await sequelize.sync({ alter: true }); 
        console.log(" Banco conectado via .env e tabelas atualizadas!");
    } catch (error) {
        console.error(" Erro ao conectar:", error);
    }
};

module.exports = { Usuario, Venda, Item, conectarBanco };