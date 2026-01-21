import api from './api'; 
import { jwtDecode } from "jwt-decode"; 
import { ENDPOINTS } from "./endpoints"; 

const AuthService = {
  login: async (user, password) => {
    try {
      

      const response = await api.post(ENDPOINTS.AUTH.LOGIN, {
        user: user,
        password: password
      });

      console.log("3. Resposta recebida do servidor:", response);

      if (response.data.token) {
        localStorage.setItem("salesToken", response.data.token);
        
        try {
            const userData = jwtDecode(response.data.token);
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