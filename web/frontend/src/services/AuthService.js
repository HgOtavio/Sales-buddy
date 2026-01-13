import api from './api'; 

import { jwtDecode } from "jwt-decode"; 

const AuthService = {
  login: async (user, password) => {
    try {
      console.log("1. Iniciando login no AuthService...");
      console.log("2. Enviando dados:", { user, password });

      const response = await api.post("/login", {
        user: user,
        password: password
      });

      console.log("3. Resposta recebida do servidor:", response);

      if (response.data.token) {
        console.log("4. Token encontrado! Salvando...");
        localStorage.setItem("salesToken", response.data.token);
        
        try {
            const userData = jwtDecode(response.data.token);
            console.log("5. Token decodificado com sucesso:", userData);
            localStorage.setItem("userData", JSON.stringify(userData));
        } catch (decodeError) {
            console.error("ERRO AO DECODIFICAR TOKEN:", decodeError);
        }
      } else {
        console.warn(" O servidor respondeu, mas não mandou token!");
      }

      return response.data;
      
    } catch (error) {
      console.error(" ERRO FATAL NO AUTHSERVICE:", error);
      throw error;
    }
  },

  logout: () => {
    localStorage.removeItem("salesToken");
    localStorage.removeItem("userData");
    window.location.href = "/login";
  },

  getCurrentUser: () => {
    const userStr = localStorage.getItem("userData");
    if (userStr) return JSON.parse(userStr);
    return null;
  }
};

export default AuthService;