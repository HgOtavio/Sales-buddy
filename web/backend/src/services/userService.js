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
    // 1. Limpeza e Validações de Formato
    const cleanTaxId = String(taxId).replace(/[^\d]+/g, '');

    if (!isValidEmail(email)) {
        throw { status: 400, message: `O formato do e-mail '${email}' é inválido.` };
    }

    if (!isValidCNPJ(cleanTaxId)) {
        throw { status: 400, message: `O CNPJ '${taxId}' informado não é válido.` };
    }

    // 2. Lógica da Empresa (Verificação de conflitos de Nome vs CNPJ)
    let companyRecord = null;
    const companyByCnpj = await Company.findOne({ where: { taxId: cleanTaxId } });
    const companyByName = await Company.findOne({ where: { name: company } });

    if (companyByCnpj) {
        // Se achou CNPJ, usamos essa empresa
        companyRecord = companyByCnpj;
        
        // Verificação de segurança: Nome bate? (Opcional, mas bom para avisar)
        if (companyByName && companyByName.id !== companyByCnpj.id) {
             // Caso raro: Nome existe em outra empresa diferente
             throw { status: 409, message: `Conflito: O CNPJ pertence à empresa ID ${companyByCnpj.id}, mas o nome '${company}' já é usado por outra empresa.` };
        }
    } else {
        // Se o CNPJ é novo, o nome não pode existir
        if (companyByName) {
            throw { 
                status: 409, 
                message: `O nome de empresa "${company}" já está cadastrado no sistema (com outro CNPJ). Use um nome diferente (ex: ${company} Filial).` 
            };
        }
        // Cria nova empresa
        companyRecord = await Company.create({
            name: company,
            taxId: cleanTaxId
        });
    }

    // 3. Duplicidade de Usuário
    const duplicateError = await checkDuplicates(user, email);
    if (duplicateError) {
        throw { status: 409, message: duplicateError };
    }

    // 4. Criação do Usuário (Senha temporária)
    const tempInternalPass = await bcrypt.hash("aguardando_ativacao_" + Date.now(), 10);

    const newUser = await User.create({
        name, 
        user, 
        email, 
        companyId: companyRecord.id, 
        password: tempInternalPass 
    });

    // 5. Token e Email
    const activationToken = jwt.sign(
        { id: newUser.id, purpose: 'activate_account' }, 
        process.env.JWT_SECRET,
        { expiresIn: '24h' }
    );

    const tokenHash = await bcrypt.hash(activationToken, 10);
    newUser.password = tokenHash;
    await newUser.save();

    try {
        await emailService.sendWelcomeTokenEmail(email, user, activationToken);
        return { message: "Cadastro realizado com sucesso! Um convite foi enviado para o e-mail." };
    } catch (mailError) {
        console.error("Erro no envio de email:", mailError);
        return { message: "Usuário cadastrado, mas houve erro ao enviar e-mail de ativação.", warning: true };
    }
};

exports.updateUser = async (targetId, requesterId, data) => {
    const { name, user, company, email, taxId } = data;
    
    // Verifica existência
    const targetUser = await User.findByPk(targetId, { include: Company });
    if (!targetUser) throw { status: 404, message: `Usuário com ID ${targetId} não encontrado.` };

    // Lógica de Empresa (Restrita ao Admin/Dono)
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

        // Verifica se CNPJ novo já existe em outra empresa
        const existingCompany = await Company.findOne({ where: { taxId: effectiveTaxId } });
        if (existingCompany && String(existingCompany.id) !== String(targetUser.companyId)) {
             throw { status: 409, message: `O CNPJ ${effectiveTaxId} já pertence a outra empresa cadastrada.` };
        }

        const userCompany = await Company.findByPk(targetUser.companyId);
        userCompany.name = effectiveName;
        userCompany.taxId = effectiveTaxId;
        await userCompany.save();
    }

    // Atualização de Usuário
    if (name) targetUser.name = name;
    if (user) targetUser.user = user;
    if (email) {
        if (!isValidEmail(email)) throw { status: 400, message: "Novo e-mail inválido." };
        targetUser.email = email;
    }
    
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
    
    // Retornamos o token e os dados do user para facilitar no Front/Postman
    return { token, user: payload };
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

    if (!process.env.JWT_SECRET) {
        throw { status: 500, message: "Erro de configuração no servidor (JWT_SECRET)." };
    }

    const resetToken = jwt.sign(
        { id: targetUser.id, purpose: 'password_reset' }, 
        process.env.JWT_SECRET
    );

    try {
        await emailService.sendResetTokenEmail(email, resetToken);
        return { message: "Token de recuperação enviado para o e-mail." };
    } catch (mailError) {
        throw { status: 500, message: "Erro ao conectar com servidor de e-mail." };
    }
};

exports.resetPassword = async (token, newPassword, confirmPassword) => {
    if (newPassword !== confirmPassword) {
        throw { status: 400, message: "A nova senha e a confirmação não coincidem." };
    }

    let decoded;
    try {
        decoded = jwt.verify(token, process.env.JWT_SECRET);
    } catch (err) {
        throw { status: 401, message: "Token inválido ou expirado." };
    }
    
    const targetUser = await User.findByPk(decoded.id);
    if (!targetUser) {
        throw { status: 404, message: "Usuário não encontrado." };
    }

    const salt = await bcrypt.genSalt(10);
    targetUser.password = await bcrypt.hash(newPassword, salt);
    
    await targetUser.save();
    return { message: "Senha atualizada com sucesso! Agora você pode fazer login." };
};

exports.verifySessionUser = async (userId) => {
    if (!User) throw new Error("Model User não definido");
    
    const user = await User.findByPk(userId);
    if (!user) throw { status: 401, message: "Sessão inválida: Usuário não existe mais." };
    
    return { id: user.id, name: user.name };
};