const { sequelize, User } = require('./src/models'); 
const bcrypt = require('bcryptjs');
require('dotenv').config();

const createMasterUser = async () => {
    try {
        await sequelize.authenticate(); 
        
        const passwordHash = await bcrypt.hash("123", 10);
        
        await User.create({
            name: "Super Admin",
            user: "admin",
            company: "SalesBuddy HQ",
            email: "admin@salesbuddy.com",
            taxId: "00000000000",
            password: passwordHash
        });

        console.log("Usuário Mestre criado com sucesso!");
        process.exit(); 
    } catch (error) {
        console.error("Erro:", error);
    }
};

createMasterUser();