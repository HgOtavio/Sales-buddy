// src/dtos/userDTO.js

// --- DA TELA PARA A API (REQUESTS) ---

export const toCreateUserRequest = (formData) => {
    return {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        company: formData.company,
        // Garante que manda 'user' mesmo que o form tenha 'username'
        user: formData.user || formData.username, 
        taxId: formData.taxId || formData.cnpj
    };
};

export const toUpdateUserRequest = (formData) => {
    return {
        id: formData.id,
        name: formData.name,
        email: formData.email,
        user: formData.user || formData.username,
        company: formData.company,
        taxId: formData.taxId || formData.cnpj
    };
};



export const toUserUI = (apiData) => {
    return {
        // Normaliza os dados para a tabela não quebrar
        id: apiData.id,
        name: apiData.name || "Nome não informado",
        email: apiData.email || "Sem e-mail",
        
        // Se o backend mandar 'username' ou 'user', a tela sempre lerá 'user'
        user: apiData.user || apiData.username || "-", 
        
       company: apiData.company || apiData.empresa || "Não informada",
        
        // O backend pode mandar como 'taxId' ou 'cnpj', aqui garantimos que a tela recebe 'taxId'
        taxId: apiData.taxId || apiData.cnpj || "", 
        
      
    };
};