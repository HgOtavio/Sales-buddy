const jwt = require('jsonwebtoken');
const { User } = require('../models'); // <--- 1. IMPORTANTE: Importe seu Model de Usuário aqui

exports.verifyToken = (req, res, next) => {
    const authHeader = req.headers.authorization;

    if (!authHeader)
        return res.status(401).json({ error: "No token provided" });

    const parts = authHeader.split(' ');

    if (parts.length !== 2)
        return res.status(401).json({ error: "Token error" });

    const [scheme, token] = parts;

    if (!/^Bearer$/i.test(scheme))
        return res.status(401).json({ error: "Token malformed" });

    // Adicionei 'async' aqui para poder consultar o banco
    jwt.verify(token, process.env.JWT_SECRET, async (err, decoded) => {
        if (err) return res.status(401).json({ error: "Token invalid" });

        try {
            // --- 2. SEGURANÇA EXTRA: Verifica se o usuário ainda existe no Banco ---
            const userExists = await User.findByPk(decoded.id);

            if (!userExists) {
                // Se o usuário foi deletado, retornamos 401.
                // Isso vai acionar o SessionManager no Android e expulsar ele.
                return res.status(401).json({ error: "Usuário não encontrado ou deletado." });
            }
            // ----------------------------------------------------------------------

            req.userId = decoded.id;
            req.user = decoded;

            return next();

        } catch (dbError) {
            console.error("Erro ao verificar usuário no banco:", dbError);
            return res.status(500).json({ error: "Erro interno ao validar sessão." });
        }
    });
};