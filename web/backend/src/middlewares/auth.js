const jwt = require('jsonwebtoken');
const { User } = require('../models'); 

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

    
    jwt.verify(token, process.env.JWT_SECRET, async (err, decoded) => {
        if (err) return res.status(401).json({ error: "Token invalid" });

        try {
       
            const userExists = await User.findByPk(decoded.id);

            if (!userExists) {
              
                return res.status(401).json({ error: "Usuário não encontrado ou deletado." });
            }
           

            req.userId = decoded.id;
            req.user = decoded;

            return next();

        } catch (dbError) {
            console.error("Erro ao verificar usuário no banco:", dbError);
            return res.status(500).json({ error: "Erro interno ao validar sessão." });
        }
    });
};