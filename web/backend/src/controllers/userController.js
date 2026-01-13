const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');
const User = require('../models/User');

exports.register = async (req, res) => {
    try {
        const { name, user, company, email, taxId } = req.body;

        if (!name || !user || !email) {
            return res.status(400).json({ error: "Name, user and email are required" });
        }

        const userExists = await User.findOne({ where: { user } });
        if (userExists) {
            return res.status(409).json({ error: "Nickname already exists" });
        }

        const emailExists = await User.findOne({ where: { email } });
        if (emailExists) {
            return res.status(409).json({ error: "Email already exists" });
        }

        const randomPassword = crypto.randomBytes(4).toString('hex');
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(randomPassword, salt);

        await User.create({
            name,
            user,
            company,
            email,
            taxId,
            password: hashedPassword
        });

        console.log(`SENHA GERADA PARA ${user}: ${randomPassword}`);

        res.status(201).json({
            message: "User registered successfully"
        });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Internal Server Error", details: error.message });
    }
};

exports.login = async (req, res) => {
    try {
        const { user, password } = req.body;

        if (!user || !password) {
            return res.status(400).json({ error: "User and password are required" });
        }

        const targetUser = await User.findOne({ where: { user } });
        
        if (!targetUser) {
            return res.status(401).json({ error: "Invalid user or password" });
        }

        const isMatch = await bcrypt.compare(password, targetUser.password);
        if (!isMatch) {
            return res.status(401).json({ error: "Invalid user or password" });
        }

        const token = jwt.sign({ 
            id: targetUser.id,
            name: targetUser.name,
            user: targetUser.user,
            company: targetUser.company,
            email: targetUser.email
        }, process.env.JWT_SECRET, {
            expiresIn: '1d'
        });

        res.json({ token });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Login failed", details: error.message });
    }
};

exports.getAllUsers = async (req, res) => {
    try {
        const users = await User.findAll({
            attributes: { exclude: ['password'] } 
        });
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: "Erro ao buscar usuários" });
    }
};

exports.updateUser = async (req, res) => {
    try {
        const { id } = req.params; 
        const { name, user, company, email, taxId } = req.body; 

        const targetUser = await User.findByPk(id);

        if (!targetUser) {
            return res.status(404).json({ error: "Usuário não encontrado" });
        }

        targetUser.name = name;
        targetUser.user = user;
        targetUser.company = company;
        targetUser.email = email;
        targetUser.taxId = taxId;

        await targetUser.save();

        res.json({ message: "Usuário atualizado com sucesso!", user: targetUser });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Erro ao atualizar usuário" });
    }
};
exports.deleteUser = async (req, res) => {
    try {
        const { id } = req.params;

        // 1. Busca o usuário
        const targetUser = await User.findByPk(id);

        if (!targetUser) {
            return res.status(404).json({ error: "Usuário não encontrado" });
        }

        // 2. Deleta do banco
        await targetUser.destroy();

        res.json({ message: "Usuário excluído com sucesso" });

    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Erro ao excluir usuário" });
    }
};