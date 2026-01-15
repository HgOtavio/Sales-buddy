const userService = require('../services/userService');

// Helper para tratar erros padronizados do Service
const handleError = (res, error) => {
    if (error.status) {
        return res.status(error.status).json({ error: error.message });
    }
    console.error("Internal Error:", error);
    return res.status(500).json({ error: "Erro interno no servidor." });
};

exports.register = async (req, res) => {
    try {
        const { name, user, company, email, taxId } = req.body;
        
        // Validação básica de entrada (campos obrigatórios) continua no controller
        if (!name || !user || !email || !taxId || !company) {
            return res.status(400).json({ error: "Missing required fields" });
        }

        const result = await userService.registerUser(req.body);
        return res.status(201).json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.updateUser = async (req, res) => {
    try {
        const { id } = req.params;
        const requesterId = req.user ? req.user.id : null;
        
        const result = await userService.updateUser(id, requesterId, req.body);
        res.json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.login = async (req, res) => {
    try {
        const { user, password } = req.body;
        const result = await userService.authenticateUser(user, password);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.deleteUser = async (req, res) => {
    try {
        const { id } = req.params;
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
        const { email } = req.body;
        const result = await userService.forgotPassword(email);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.resetPassword = async (req, res) => {
    try {
        const { token, newPassword, confirmPassword } = req.body;
        const result = await userService.resetPassword(token, newPassword, confirmPassword);
        res.json(result);
    } catch (error) {
        handleError(res, error);
    }
};

exports.verifySession = async (req, res) => {
    // Mantive os logs de debug que você tinha, mas delegando a lógica
    console.log("\n🔥🔥🔥 [INICIO] Debug verifySession 🔥🔥🔥");
    
    try {
        if (!req.user || !req.user.id) {
            console.log("❌ ERRO FATAL: req.user.id está undefined/null.");
            return res.status(401).json({ error: "Token sem ID." });
        }

        const user = await userService.verifySessionUser(req.user.id);
        
        console.log(`✅ SUCESSO TOTAL: Usuário encontrado: ${user.name}`);
        console.log("🔥🔥🔥 [FIM] Debug verifySession 🔥🔥🔥\n");
        
        return res.json({ valid: true, user });

    } catch (error) {
        console.log("💀💀💀 CRASH/EXCEÇÃO NO CONTROLLER 💀💀💀");
        console.log("ERRO REAL:", error.message);
        handleError(res, error);
    }
};