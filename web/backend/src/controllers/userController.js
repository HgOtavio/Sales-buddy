const userService = require('../services/userService');

// --- HELPERS ---

// 1. Tratamento de erro padronizado
const handleError = (res, error) => {
    if (error.status) {
        return res.status(error.status).json({ 
            error: "Erro na Operação",
            message: error.message 
        });
    }
    console.error("Internal Error:", error);
    return res.status(500).json({ error: "Erro interno no servidor." });
};

// 2. Validador de Campos Obrigatórios
// Retorna uma string com o erro ou null se estiver tudo ok
const validateRequiredFields = (body, requiredFields) => {
    if (!body || Object.keys(body).length === 0) {
        return "O corpo da requisição (JSON) está vazio. Verifique se você selecionou 'raw' > 'JSON' no Postman.";
    }
    const missing = requiredFields.filter(field => !body[field]);
    if (missing.length > 0) {
        return `Os seguintes campos são obrigatórios e estão faltando: ${missing.join(', ')}`;
    }
    return null;
};

// --- CONTROLLERS ---

exports.register = async (req, res) => {
    try {
        // Validação: Campos obrigatórios
        const errorMsg = validateRequiredFields(req.body, ['name', 'user', 'company', 'email', 'taxId']);
        if (errorMsg) {
            return res.status(400).json({ error: "Dados Incompletos", message: errorMsg });
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
        
        // Validação: Tentar atualizar sem mandar JSON nenhum
        if (!req.body || Object.keys(req.body).length === 0) {
            return res.status(400).json({ 
                error: "Nada para atualizar", 
                message: "Envie pelo menos um campo no JSON para ser atualizado." 
            });
        }

        const result = await userService.updateUser(id, requesterId, req.body);
        res.json(result);

    } catch (error) {
        handleError(res, error);
    }
};

exports.login = async (req, res) => {
    try {
        // Validação: Login precisa de user e password
        const errorMsg = validateRequiredFields(req.body, ['user', 'password']);
        if (errorMsg) {
            return res.status(400).json({ error: "Credenciais Faltando", message: errorMsg });
        }

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
    console.log("\n🔥🔥🔥 [INICIO] Debug verifySession 🔥🔥🔥");
    try {
        if (!req.user || !req.user.id) {
            console.log("❌ ERRO: Token decodificado mas sem ID.");
            return res.status(401).json({ error: "Sessão inválida (Token sem ID)." });
        }

        const user = await userService.verifySessionUser(req.user.id);
        
        console.log(`✅ Usuário verificado: ${user.name}`);
        console.log("🔥🔥🔥 [FIM] Debug verifySession 🔥🔥🔥\n");
        
        return res.json({ valid: true, user });

    } catch (error) {
        console.log("💀 Erro no verifySession:", error.message);
        handleError(res, error);
    }
};