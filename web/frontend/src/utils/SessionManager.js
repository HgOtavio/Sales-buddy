import { jwtDecode } from "jwt-decode";

export const SessionManager = {
  // Salva o token e os dados do usuário
  saveSession: (token) => {
    try {
      localStorage.setItem("salesToken", token);
      
      // Decodifica o token para pegar ID/Nome do usuário
      const userData = jwtDecode(token);
      localStorage.setItem("userData", JSON.stringify(userData));
      
      return userData;
    } catch (error) {
      console.error("ERRO AO DECODIFICAR TOKEN:", error);
      return null;
    }
  },

  // Limpa tudo (Logout)
  clearSession: () => {
    localStorage.removeItem("salesToken");
    localStorage.removeItem("userData");
  },

  // Pega o usuário salvo
  getUser: () => {
    const userStr = localStorage.getItem("userData");
    return userStr ? JSON.parse(userStr) : null;
  },

  // Pega só o token (útil para cabeçalhos)
  getToken: () => {
    return localStorage.getItem("salesToken");
  }
};