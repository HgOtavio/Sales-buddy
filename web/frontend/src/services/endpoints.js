export const ENDPOINTS = {
  AUTH: {
    LOGIN: "/auth/login",
    RESET_PASSWORD: "/auth/reset-password", // Definir nova senha
    VERIFY: "/auth/verify",
    REGISTER: "/auth/register",             // Criar conta
    FORGOT_PASSWORD: "/auth/forgot-password", // Pedir link de reset
    USERS: "/auth/users"                    // Base para update (/auth/users/:id)
  },
  // VENDAS: { ... }
};