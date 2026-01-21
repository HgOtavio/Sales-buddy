// middlewares/serverMiddleware.js

const handleJsonSyntaxError = (err, req, res, next) => {
    if (err instanceof SyntaxError && err.status === 400 && 'body' in err) {
        return res.status(400).json({ 
            error: "JSON Inválido",
            message: "Há um erro de sintaxe no seu JSON. Verifique vírgulas sobrando ou aspas faltantes." 
        });
    }
    next();
};

const handleUrlErrors = (req, res, next) => {
    if (req.originalUrl.includes('%20') || req.originalUrl.endsWith(' ')) {
        return res.status(400).json({ 
            error: "URL Mal Formatada",
            message: "Detectamos um espaço em branco na sua URL. Verifique no Postman se não há um espaço sobrando no final do endereço." 
        });
    }
    next();
};

const handle404 = (req, res) => {
    res.status(404).json({ 
        error: "Rota não encontrada", 
        message: `A rota '${req.originalUrl}' não existe ou o método (${req.method}) está incorreto.` 
    });
};

module.exports = {
    handleJsonSyntaxError,
    handleUrlErrors,
    handle404
};