const { sequelize, User } = require('./src/models'); 
const bcrypt = require('bcryptjs');
require('dotenv').config();

const resetAndCreateMaster = async () => {
    try {
        await sequelize.authenticate();
        
        await sequelize.sync({ force: true });
        
        const passwordHash = await bcrypt.hash("123", 10);
        
        await User.create({
            name: "Super Admin",
            user: "admin",
            company: "SalesBuddy HQ",
            email: "admin@salesbuddy.com",
            taxId: "00.000.000/0000-00",
            password: passwordHash
        });

        console.log("Banco de dados resetado e Usuário Mestre criado com ID 1!");
        process.exit(); 
    } catch (error) {
        console.error("Erro ao resetar e criar usuário:", error);
        process.exit(1);
    }
};

resetAndCreateMaster();