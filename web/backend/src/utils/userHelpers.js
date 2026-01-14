const { Op } = require('sequelize');
const User = require('../models/User');

// Função "Standard Gold" de validação de CNPJ
const isValidCNPJ = (cnpj) => {
    if (!cnpj) return false;

    // 1. Deixa apenas números
    cnpj = String(cnpj).replace(/[^\d]+/g, '');

    // 2. Valida tamanho e números repetidos
    if (cnpj.length !== 14) return false;
    if (/^(\d)\1+$/.test(cnpj)) return false;

    // 3. Validação do Primeiro Dígito
    let tamanho = cnpj.length - 2;
    let numeros = cnpj.substring(0, tamanho);
    let digitos = cnpj.substring(tamanho);
    let soma = 0;
    let pos = tamanho - 7;

    for (let i = tamanho; i >= 1; i--) {
        soma += parseInt(numeros.charAt(tamanho - i)) * pos--;
        if (pos < 2) pos = 9;
    }

    let resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
    if (resultado !== parseInt(digitos.charAt(0))) return false;

    // 4. Validação do Segundo Dígito
    tamanho = tamanho + 1;
    numeros = cnpj.substring(0, tamanho);
    soma = 0;
    pos = tamanho - 7;

    for (let i = tamanho; i >= 1; i--) {
        soma += parseInt(numeros.charAt(tamanho - i)) * pos--;
        if (pos < 2) pos = 9;
    }

    resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11);
    if (resultado !== parseInt(digitos.charAt(1))) return false;

    return true;
};

const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) return false;
    
    const blockedDomains = ['tempmail.com', '10minutemail.com'];
    const domain = email.split('@')[1];
    return !blockedDomains.includes(domain);
};

const generateStrongPassword = (length = 12) => {
    const charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_+";
    let password = "";
    for (let i = 0; i < length; ++i) {
        password += charset.charAt(Math.floor(Math.random() * charset.length));
    }
    return password;
};

const checkDuplicates = async (user, email, taxId, company, excludeUserId = null) => {
    const cleanTaxId = String(taxId).replace(/[^\d]+/g, ''); 
    
    const query = {
        [Op.or]: [
            { user: user },
            { email: email },
            { 
                [Op.and]: [
                    { taxId: cleanTaxId },
                    { company: { [Op.ne]: company } }
                ] 
            }
        ]
    };

    if (excludeUserId) {
        query.id = { [Op.ne]: excludeUserId };
    }

    const existingUser = await User.findOne({ where: query });

    if (existingUser) {
        if (existingUser.user === user) return "Nickname (usuário) já existe.";
        if (existingUser.email === email) return "E-mail já cadastrado.";
        if (existingUser.taxId === cleanTaxId && existingUser.company !== company) {
            return "Este CNPJ já está vinculado a outra empresa.";
        }
    }
    return null;
};

module.exports = {
    isValidCNPJ,
    isValidEmail,
    generateStrongPassword,
    checkDuplicates
};