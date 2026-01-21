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

module.exports = { handleError };