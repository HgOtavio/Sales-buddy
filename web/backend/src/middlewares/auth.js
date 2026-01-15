const jwt = require('jsonwebtoken');

// IMPORTANTE: Use exports.verifyToken para funcionar com o seu arquivo de rotas
exports.verifyToken = (req, res, next) => {
    const authHeader = req.headers.authorization;

    if (!authHeader)
        return res.status(401).json({ error: "No token provided" });

    // Divide o header em duas partes: "Bearer" e o "token"
    const parts = authHeader.split(' ');

    // A lógica original (!parts.length === 2) estava perigosa. O correto é verificar se é diferente de 2.
    if (parts.length !== 2)
        return res.status(401).json({ error: "Token error" });

    const [scheme, token] = parts;

    if (!/^Bearer$/i.test(scheme))
        return res.status(401).json({ error: "Token malformed" });

    jwt.verify(token, process.env.JWT_SECRET, (err, decoded) => {
        if (err) return res.status(401).json({ error: "Token invalid" });

      
        req.userId = decoded.id; 
        req.user = decoded; 

        return next();
    });
};