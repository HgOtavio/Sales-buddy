const { Op } = require('sequelize');
const User = require('../models/User');

const isValidCPF = (cpf) => {
    if (!cpf) return false;

    const strCPF = String(cpf).replace(/[^\d]+/g, '');

    if (strCPF.length !== 11 || /^(\d)\1+$/.test(strCPF)) return false;

    let sum = 0;
    let remainder;

    for (let i = 1; i <= 9; i++) sum = sum + parseInt(strCPF.substring(i - 1, i)) * (11 - i);
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(strCPF.substring(9, 10))) return false;

    sum = 0;
    for (let i = 1; i <= 10; i++) sum = sum + parseInt(strCPF.substring(i - 1, i)) * (12 - i);
    remainder = (sum * 10) % 11;
    if ((remainder === 10) || (remainder === 11)) remainder = 0;
    if (remainder !== parseInt(strCPF.substring(10, 11))) return false;

    return true;
};

const isValidCNPJ = (cnpj) => {
    if (!cnpj) return false;

    const strCNPJ = String(cnpj).replace(/[^\d]+/g, '');

    if (strCNPJ.length !== 14) return false;
    if (/^(\d)\1+$/.test(strCNPJ)) return false;

    const weights1 = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const weights2 = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];

    let sum = 0;
    for (let i = 0; i < 12; i++) {
        sum += parseInt(strCNPJ[i]) * weights1[i];
    }
    let remainder = sum % 11;
    let digit1 = remainder < 2 ? 0 : 11 - remainder;

    if (parseInt(strCNPJ[12]) !== digit1) return false;

    sum = 0;
    for (let i = 0; i < 13; i++) {
        sum += parseInt(strCNPJ[i]) * weights2[i];
    }
    remainder = sum % 11;
    let digit2 = remainder < 2 ? 0 : 11 - remainder;

    if (parseInt(strCNPJ[13]) !== digit2) return false;

    return true;
};

const isValidEmail = (email) => {
    if (!email) return false;

    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.(com|com\.br|net|org|edu)$/i;
    if (!emailRegex.test(email)) return false;
    
    const blockedDomains = ['tempmail.com', '10minutemail.com', 'mailinator.com'];
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
    const query = {
        [Op.or]: [
            { user: user },
            { email: email }
        ]
    };

    if (excludeUserId) {
        query.id = { [Op.ne]: excludeUserId };
    }

    const existingUser = await User.findOne({ where: query });

    if (existingUser) {
        if (existingUser.user === user) return "Nome de usuário já existe.";
        if (existingUser.email === email) return "E-mail já cadastrado.";
    }
    return null;
};

module.exports = {
    isValidCPF,
    isValidCNPJ,
    isValidEmail,
    generateStrongPassword,
    checkDuplicates
};