require('dotenv').config();
const { sequelize } = require('./src/config/dbConfig');
const User = require('./src/models/User');
const Company = require('./src/models/Company'); // Importar o modelo Company
const bcrypt = require('bcryptjs');

const resetAndCreateMaster = async () => {
    try {
        await sequelize.authenticate();
        console.log('Conectado ao banco...');

        // Limpa o banco e recria as tabelas (Cuidado: Apaga tudo!)
        await sequelize.sync({ force: true });
        console.log('Tabelas resetadas.');

        // 1. CRIA A EMPRESA PADRÃO (Obrigatório agora)
        const adminCompany = await Company.create({
            name: "SalesBuddy HQ (Admin)",
            taxId: "00000000000191" // Um CNPJ fictício para o admin
        });

        console.log(`Empresa Admin criada com ID: ${adminCompany.id}`);

        // 2. CRIA A SENHA HASH
        const hashedPassword = await bcrypt.hash("admin123", 10);

        // 3. CRIA O USUÁRIO VINCULADO À EMPRESA
        await User.create({
            name: "Super Admin",
            user: "admin",
            email: "admin@salesbuddy.com",
            password: hashedPassword,
            companyId: adminCompany.id // <--- AQUI ESTÁ A CORREÇÃO (Vínculo)
        });

        console.log('>>> Sucesso! Usuário "admin" criado.');
        console.log('Login: admin');
        console.log('Senha: admin123');
        console.log('Empresa: SalesBuddy HQ (Admin)');

    } catch (error) {
        console.error('Erro ao criar usuário:', error);
    } finally {
        await sequelize.close();
    }
};

export { resetAndCreateMaster };