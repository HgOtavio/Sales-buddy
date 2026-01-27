// src/dtos/authDTO.js

// 1. Para o Login
export const toLoginRequest = (user, password) => {
    return {
        user: user ? user.trim() : "",
        password: password
    };
};

// 2. Para o Esqueci Minha Senha (A QUE ESTÁ FALTANDO)
export const toForgotPasswordRequest = (email) => {
    return {
        email: email ? email.trim() : "" 
    };
};

// 3. Para o Resetar Senha
export const toResetPasswordRequest = (token, newPassword) => {
    return {
        token: token ? token.trim() : "",
        newPassword: newPassword
        // confirmPassword não vai para o backend
    };
};