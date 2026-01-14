const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const emailService = require('../services/emailService');

const { 
    isValidEmail, 
    generateStrongPassword, 
    isValidCNPJ,
    checkDuplicates 
} = require('../utils/userHelpers');
exports.register = async (req, res) => {
    try {
        let { name, user, company, email, taxId } = req.body;
        
        if (!name || !user || !email || !taxId || !company) {
            return res.status(400).json({ error: "Missing required fields" });
        }

      
        const cleanTaxId = taxId.replace(/[^\d]+/g, '');

        if (!isValidEmail(email)) {
            return res.status(400).json({ error: "Invalid email format or domain" });
        }

        if (!isValidCNPJ(cleanTaxId)) {
            return res.status(400).json({ error: "Invalid CNPJ format or number" });
        }

        const firstCompanyRecord = await User.findOne({
            where: { company: company },
            order: [['createdAt', 'ASC']]
        });

        // Comparação segura (limpo com limpo)
        if (firstCompanyRecord && firstCompanyRecord.taxId !== cleanTaxId) {
            return res.status(400).json({ 
                error: `CNPJ incorreto para a empresa ${company}. Verifique com o administrador.` 
            });
        }

        // Checagem de duplicidade enviando o CNPJ limpo
        const duplicateError = await checkDuplicates(user, email, cleanTaxId, company);
        if (duplicateError) {
            return res.status(409).json({ error: duplicateError });
        }

        const dummyPassword = await bcrypt.hash(generateStrongPassword(), 10);

        const newUser = await User.create({
            name, 
            user, 
            company, 
            email, 
            taxId: cleanTaxId, // SALVA NO BANCO SOMENTE OS NÚMEROS
            password: dummyPassword
        });

        const welcomeToken = jwt.sign(
            { id: newUser.id, purpose: 'password_reset' }, 
            process.env.JWT_SECRET
        );

        // Atenção: Use a função unificada que criamos (sendTokenEmail) ou a sendWelcomeTokenEmail se manteve a antiga
        await emailService.sendWelcomeTokenEmail(email, user, welcomeToken);

        res.status(201).json({ message: "Usuário registrado! Token enviado por e-mail." });

    } catch (error) {
        console.error("Register Error:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

exports.updateUser = async (req, res) => {
    try {
        const { id } = req.params; 
        const { name, user, company, email, taxId } = req.body; 

        const targetUser = await User.findByPk(id);
        if (!targetUser) return res.status(404).json({ error: "User not found" });

        const effectiveCompany = company || targetUser.company;
        
        // AQUI TAMBÉM: Se veio um novo taxId (com máscara), limpamos. Se não, usa o do banco.
        const effectiveTaxId = taxId ? taxId.replace(/[^\d]+/g, '') : targetUser.taxId;

        const firstUser = await User.findOne({
            where: { company: effectiveCompany },
            order: [['createdAt', 'ASC']]
        });

        if (firstUser) {
            // Lógica mantida, mas agora garantimos que estamos comparando números com números
            if (taxId && effectiveTaxId !== targetUser.taxId && targetUser.id !== firstUser.id) {
                return res.status(403).json({ error: "Apenas o primeiro usuário da empresa pode alterar o CNPJ." });
            }
            if (taxId && effectiveTaxId !== firstUser.taxId) {
                 return res.status(400).json({ error: `CNPJ não coincide com o registro da empresa ${effectiveCompany}.` });
            }
        }

        const duplicateError = await checkDuplicates(user || targetUser.user, email || targetUser.email, effectiveTaxId, effectiveCompany, id);
        if (duplicateError) return res.status(409).json({ error: duplicateError });

        targetUser.name = name || targetUser.name;
        targetUser.user = user || targetUser.user;
        targetUser.company = effectiveCompany;
        targetUser.email = email || targetUser.email;
        targetUser.taxId = effectiveTaxId; // Salva limpo

        await targetUser.save();
        res.json({ message: "User updated successfully" });
    } catch (error) {
        res.status(500).json({ error: "Error updating user" });
    }
};


exports.login = async (req, res) => {
    try {
        const { user, password } = req.body;
        const targetUser = await User.findOne({ where: { user } });
        
        if (!targetUser || !(await bcrypt.compare(password, targetUser.password))) {
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const token = jwt.sign({ 
            id: targetUser.id,
            user: targetUser.user,
            company: targetUser.company 
        }, process.env.JWT_SECRET, { expiresIn: '1d' });

        res.json({ token, user: { id: targetUser.id, user: targetUser.user, company: targetUser.company } });

    } catch (error) {
        res.status(500).json({ error: "Login failed" });
    }
};

exports.deleteUser = async (req, res) => {
    try {
        const { id } = req.params;
        const loggedId = req.user ? req.user.id : null; 

        if (loggedId && String(loggedId) === String(id)) {
            return res.status(403).json({ error: "You cannot delete your own account." });
        }

        const targetUser = await User.findByPk(id);
        if (!targetUser) return res.status(404).json({ error: "User not found" });

        await targetUser.destroy();
        res.json({ message: "User deleted successfully" });
    } catch (error) {
        res.status(500).json({ error: "Error deleting user" });
    }
};

exports.getAllUsers = async (req, res) => {
    try {
        const users = await User.findAll({ attributes: { exclude: ['password'] } });
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: "Error fetching users" });
    }
};

exports.forgotPassword = async (req, res) => {
    try {
        const { email } = req.body;
        console.log(">>> [DEBUG] Email recebido no Back:", email);

        const targetUser = await User.findOne({ where: { email } });

        if (!targetUser) {
            console.log(">>> [DEBUG] Usuário não existe no banco.");
            return res.status(404).json({ error: "Usuário não encontrado." });
        }

        if (!process.env.JWT_SECRET) {
            console.log(">>> [DEBUG] ERRO: JWT_SECRET não definida no .env");
            return res.status(500).json({ error: "Erro de configuração no servidor." });
        }

        const resetToken = jwt.sign(
            { id: targetUser.id, purpose: 'password_reset' }, 
            process.env.JWT_SECRET
        );

        console.log(">>> [DEBUG] Token gerado, tentando enviar e-mail...");

        try {
            await emailService.sendResetTokenEmail(email, resetToken);
            console.log(">>> [DEBUG] E-mail enviado com sucesso!");
            return res.json({ message: "Token enviado para o e-mail." });
        } catch (mailError) {
            console.error(">>> [DEBUG] ERRO CRÍTICO NO NODEMAILER:", mailError.message);
            return res.status(500).json({ error: "Erro ao conectar com servidor de e-mail." });
        }

    } catch (error) {
        console.error(">>> [DEBUG] ERRO GERAL NO CONTROLLER:", error);
        return res.status(500).json({ error: "Erro interno no servidor." });
    }
};

exports.resetPassword = async (req, res) => {
    try {
        const { token, newPassword, confirmPassword } = req.body;

        if (newPassword !== confirmPassword) {
            return res.status(400).json({ error: "As senhas não coincidem." });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        
        const targetUser = await User.findByPk(decoded.id);
        
        if (!targetUser) {
            return res.status(404).json({ error: "Usuário não encontrado." });
        }

        const salt = await bcrypt.genSalt(10);
        targetUser.password = await bcrypt.hash(newPassword, salt);
        
        await targetUser.save();

        res.json({ message: "Senha atualizada com sucesso!" });
    } catch (error) {
        res.status(401).json({ error: "Token inválido ou expirado." });
    }
};