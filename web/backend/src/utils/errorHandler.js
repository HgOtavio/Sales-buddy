// src/utils/errorHandler.js

exports.handleError = (res, error) => {
   
    console.error(" Erro capturado:", error);

    
    const status = error.status || 500;
   
    const message = error.message || "Erro interno do servidor.";
    
   
    const extra = {};
    if (error.missing) extra.missing = error.missing;
    if (error.errors) extra.details = error.errors; 

    return res.status(status).json({
        error: "Ocorreu um erro",
        message: message,
        ...extra
    });
};