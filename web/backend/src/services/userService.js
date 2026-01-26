const crypto = require('crypto');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Company = require('../models/Company');
const emailService = require('./emailService');
const { 
    isValidEmail, 
    isValidCNPJ,
    checkDuplicates 
} = require('../utils/userHelpers');

exports.registerUser = async ({ name, user, company, email, taxId }) => {
    const cleanTaxId = String(taxId).replace(/[^\d]+/g, '');

    if (!isValidEmail(email)) {
        throw { status: 400, message: `O formato do e-mail '${email}' é inválido.` };
    }

    if (!isValidCNPJ(cleanTaxId)) {
        throw { status: 400, message: `O CNPJ '${taxId}' informado não é válido.` };
    }

    let companyRecord = null;
    const companyByCnpj = await Company.findOne({ where: { taxId: cleanTaxId } });
    const companyByName = await Company.findOne({ where: { name: company } });

    if (companyByCnpj) {
        companyRecord = companyByCnpj;
        
        if (companyByName && companyByName.id !== companyByCnpj.id) {
             throw { status: 409, message: `Conflito: O CNPJ pertence à empresa ID ${companyByCnpj.id}, mas o nome '${company}' já é usado por outra empresa.` };
        }
    } else {
        if (companyByName) {
            throw { 
                status: 409, 
                message: `O nome de empresa "${company}" já está cadastrado no sistema (com outro CNPJ). Use um nome diferente.` 
            };
        }
        companyRecord = await Company.create({
            name: company,
            taxId: cleanTaxId
        });
    }

    const duplicateError = await checkDuplicates(user, email);
    if (duplicateError) {
        throw { status: 409, message: duplicateError };
    }

    const tempInternalPass = await bcrypt.hash("aguardando_ativacao_" + Date.now(), 10);

    const newUser = await User.create({
        name, 
        user, 
        email, 
        companyId: companyRecord.id, 
        password: tempInternalPass 
    });

    const activationToken = jwt.sign(
        { id: newUser.id, purpose: 'activate_account' }, 
        process.env.JWT_SECRET,
        { expiresIn: '24h' }
    );

    const tokenHash = await bcrypt.hash(activationToken, 10);
    newUser.password = tokenHash;
    await newUser.save();

    emailService.sendWelcomeTokenEmail(email, user, activationToken)
        .catch(err => console.error(err));

    return { message: "Cadastro realizado com sucesso! Um convite foi enviado para o e-mail." };
};

exports.updateUser = async (targetId, requesterId, data) => {
    const { name, user, company, email, taxId } = data;
    
    const targetUser = await User.findByPk(targetId, { include: Company });
    if (!targetUser) throw { status: 404, message: `Usuário com ID ${targetId} não encontrado.` };

    if (company || taxId) {
        const ownerUser = await User.findOne({
            where: { companyId: targetUser.companyId },
            order: [['id', 'ASC']]
        });
        
        if (ownerUser && String(ownerUser.id) !== String(requesterId)) {
            throw { 
                status: 403, 
                message: "Permissão negada: Apenas o administrador principal da empresa pode alterar a Razão Social ou CNPJ." 
            };
        }

        const effectiveTaxId = taxId ? String(taxId).replace(/[^\d]+/g, '') : targetUser.Company.taxId;
        const effectiveName = company || targetUser.Company.name;

        if (taxId && !isValidCNPJ(effectiveTaxId)) {
            throw { status: 400, message: "O novo CNPJ informado é inválido." };
        }

        const existingCompany = await Company.findOne({ where: { taxId: effectiveTaxId } });
        if (existingCompany && String(existingCompany.id) !== String(targetUser.companyId)) {
             throw { status: 409, message: `O CNPJ ${effectiveTaxId} já pertence a outra empresa cadastrada.` };
        }

        const userCompany = await Company.findByPk(targetUser.companyId);
        userCompany.name = effectiveName;
        userCompany.taxId = effectiveTaxId;
        await userCompany.save();
    }

    if (email && email !== targetUser.email) {
        if (!isValidEmail(email)) throw { status: 400, message: "Novo e-mail inválido." };

        const emailExists = await User.findOne({ where: { email } });
        if (emailExists && emailExists.id !== targetUser.id) {
            throw { status: 409, message: "Este e-mail já está em uso por outro usuário." };
        }
        targetUser.email = email;
    }

    if (user && user !== targetUser.user) {
        const userExists = await User.findOne({ where: { user } });
        if (userExists && userExists.id !== targetUser.id) {
            throw { status: 409, message: "Este nome de usuário já está em uso." };
        }
        targetUser.user = user;
    }

    if (name) targetUser.name = name;
    
    await targetUser.save();
    return { message: "Dados atualizados com sucesso!" };
};

exports.authenticateUser = async (username, password) => {
    const targetUser = await User.findOne({ 
        where: { user: username },
        include: Company 
    });
    
    if (!targetUser || !(await bcrypt.compare(password, targetUser.password))) {
        throw { status: 401, message: "Usuário ou senha incorretos." };
    }

    const payload = {
        id: targetUser.id,
        name: targetUser.name,
        user: targetUser.user,
        email: targetUser.email,
        companyId: targetUser.companyId,
        companyName: targetUser.Company ? targetUser.Company.name : "Sem Empresa",
        taxId: targetUser.Company ? targetUser.Company.taxId : "N/A"
    };

    const token = jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: '1d' });
    
    return { token };
};

exports.deleteUser = async (targetId, requesterId) => {
    if (requesterId && String(requesterId) === String(targetId)) {
        throw { status: 403, message: "Você não pode deletar sua própria conta." };
    }

    const targetUser = await User.findByPk(targetId);
    if (!targetUser) throw { status: 404, message: "Usuário não encontrado para exclusão." };

    await targetUser.destroy();
    return { message: "Usuário deletado com sucesso." };
};

exports.getAllUsers = async () => {
    const users = await User.findAll({ 
        attributes: { exclude: ['password'] },
        include: Company
    });
    
    return users.map(u => ({
        id: u.id,
        name: u.name,
        user: u.user,
        email: u.email,
        company: u.Company ? u.Company.name : null,
        taxId: u.Company ? u.Company.taxId : null,
        createdAt: u.createdAt,
        updatedAt: u.updatedAt
    }));
};

exports.forgotPassword = async (email) => {
    const targetUser = await User.findOne({ where: { email } });

    if (!targetUser) {
        throw { status: 404, message: "Nenhum usuário encontrado com este e-mail." };
    }

    const now = Date.now();

    if (targetUser.resetToken && targetUser.resetTokenExpires > now) {
        const timeLeft = Math.ceil((targetUser.resetTokenExpires - now) / 60000); 
        throw { 
            status: 429, 
            message: `Você já solicitou a recuperação de senha. Aguarde ${timeLeft} minuto(s) para tentar novamente.` 
        };
    }

    const resetToken = crypto.randomBytes(32).toString('hex');
    const expiry = now + (15 * 60 * 1000); 

    targetUser.resetToken = resetToken;
    targetUser.resetTokenExpires = expiry;
    await targetUser.save();

    emailService.sendResetTokenEmail(email, resetToken)
        .catch(err => console.error(err));

    return { message: "Token de recuperação enviado para o e-mail." };
};

exports.resetPassword = async (token, newPassword, confirmPassword) => {
    if (newPassword !== confirmPassword) {
        throw { status: 400, message: "A nova senha e a confirmação não coincidem." };
    }

    const targetUser = await User.findOne({ where: { resetToken: token } });
    if (!targetUser) {
        throw { status: 400, message: "Token inválido ou não encontrado." };
    }

    if (targetUser.resetTokenExpires < Date.now()) {
        throw { status: 400, message: "Este link expirou. Por favor, solicite um novo." };
    }

    const salt = await bcrypt.genSalt(10);
    targetUser.password = await bcrypt.hash(newPassword, salt);
    targetUser.resetToken = null;
    targetUser.resetTokenExpires = null;
    
    await targetUser.save();
    return { message: "Senha atualizada com sucesso! Agora você pode fazer login." };
};

exports.verifySessionUser = async (userId) => {
    if (!User) throw new Error("Model User não definido");
    
    const user = await User.findByPk(userId);
    if (!user) throw { status: 401, message: "Sessão inválida: Usuário não existe mais." };
    
    return { id: user.id, name: user.name };
};