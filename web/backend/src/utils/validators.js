const validateRequiredFields = (body, requiredFields) => {
    if (!body || Object.keys(body).length === 0) {
        return "O corpo da requisição (JSON) está vazio. Verifique se você selecionou 'raw' > 'JSON' no Postman/Frontend.";
    }

    const missing = requiredFields.filter(field => !body[field]);
    
    if (missing.length > 0) {
        return `Os seguintes campos são obrigatórios e estão faltando: ${missing.join(', ')}`;
    }

    return null; 
};

module.exports = { validateRequiredFields };