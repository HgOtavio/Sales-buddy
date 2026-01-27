// src/dtos/loginDTO.js

export const toLoginRequest = (userOrEmail, password) => {
    return {
        // Regra: backend espera "user", mas removemos espaços extras
        user: userOrEmail ? userOrEmail.trim() : "", 
        password: password
    };
};