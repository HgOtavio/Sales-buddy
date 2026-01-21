const userService = require('../services/userService');

const { handleError } = require('../utils/errorHandler');
const { validateRequiredFields } = require('../utils/validators');

exports.register = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['name', 'user', 'company', 'email', 'taxId']);
        if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

        const result = await userService.registerUser(req.body);
        return res.status(201).json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.updateUser = async (req, res) => {
    try {
        // 1. Valida se o ID veio no corpo
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "ID obrigatório", message: errorMsg });

        // 2. Separa o ID do resto dos dados
        const { id, ...data } = req.body;
        const requesterId = req.user ? req.user.id : null;
        
        // 3. Verifica se tem dados para atualizar além do ID
        if (Object.keys(data).length === 0) {
            return res.status(400).json({ 
                error: "Nada para atualizar", 
                message: "Envie pelo menos um campo no JSON (além do ID) para ser atualizado." 
            });
        }

        const result = await userService.updateUser(id, requesterId, data);
        res.json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.login = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['user', 'password']);
        if (errorMsg) return res.status(400).json({ error: "Credenciais Faltando", message: errorMsg });

        const { user, password } = req.body;
        const result = await userService.authenticateUser(user, password);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.deleteUser = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['id']);
        if (errorMsg) return res.status(400).json({ error: "ID obrigatório", message: errorMsg });

        const { id } = req.body;
        const requesterId = req.user ? req.user.id : null;
        
        const result = await userService.deleteUser(id, requesterId);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.getAllUsers = async (req, res) => {
    try {
        const users = await userService.getAllUsers();
        res.json(users);
    } catch (error) {
        handleError(res, error);
    }
};

exports.forgotPassword = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['email']);
        if (errorMsg) return res.status(400).json({ error: "Campo Faltando", message: errorMsg });

        const { email } = req.body;
        const result = await userService.forgotPassword(email);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.resetPassword = async (req, res) => {
    try {
        const errorMsg = validateRequiredFields(req.body, ['token', 'newPassword', 'confirmPassword']);
        if (errorMsg) return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });

        const { token, newPassword, confirmPassword } = req.body;
        const result = await userService.resetPassword(token, newPassword, confirmPassword);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.verifySession = async (req, res) => {
    try {
        if (!req.user || !req.user.id) {
            return res.status(401).json({ error: "Sessão inválida (Token sem ID)." });
        }

        const user = await userService.verifySessionUser(req.user.id);
        return res.json({ valid: true, user });

    } catch (error) {
        handleError(res, error);
    }
};