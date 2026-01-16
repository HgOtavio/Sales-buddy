const { Sequelize } = require('sequelize');
const bcrypt = require('bcryptjs');
require('dotenv').config();

const sequelize = new Sequelize(
    process.env.DB_NAME,
    process.env.DB_USER,
    String(process.env.DB_PASS || 'postgres'), 
    {
        host: process.env.DB_HOST,
        dialect: 'postgres',
        logging: false,
    }
);

const conectarBanco = async () => {
    try {
        await sequelize.authenticate();
        console.log('✅ Conexão com banco de dados estabelecida.');

        // IMPORTANTE:
        // Apenas importamos o index.js. Ele já contém os Models e as Associações.
        // Não precisamos redefinir hasMany/belongsTo aqui.
        const { Company, User } = require('../models/index'); 

        // Sincroniza as tabelas (Cria o que falta)
        await sequelize.sync({ alter: true }); 

        // --- SEED (CRIAR ADMIN) ---
        const adminExists = await User.findOne({ where: { user: 'admin' } });

        if (!adminExists) {
            let adminCompany = await Company.findOne({ where: { taxId: "00000000000191" } });
            
            if (!adminCompany) {
                adminCompany = await Company.create({
                    name: "SalesBuddy HQ (Admin)",
                    taxId: "00000000000191"
                });
            }

            const hashedPassword = await bcrypt.hash("admin123", 10);
            
            await User.create({
                name: "Super Admin",
                user: "admin",
                email: "admin@salesbuddy.com",
                password: hashedPassword,
                companyId: adminCompany.id
            });

            console.log('🚀 SEED REALIZADO: Usuário Admin criado.');
        }

    } catch (error) {
        console.error('❌ Erro ao conectar:', error);
    }
};

module.exports = { sequelize, conectarBanco };