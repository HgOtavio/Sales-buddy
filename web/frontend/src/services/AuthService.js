import api from './api'; 
import { ENDPOINTS } from "./endpoints"; 
import { SessionManager } from "../utils/SessionManager"; // <--- Importamos o arquivo novo

const AuthService = {
  
  // Mantive (user, password) para não quebrar seu Hook atual
  login: async (user, password) => {
    try {
      // 1. Chama a API
      const response = await api.post(ENDPOINTS.AUTH.LOGIN, {
        user: user,
        password: password
      });

      console.log("Resposta recebida:", response);

      // 2. Se a API mandou token, o SessionManager cuida de salvar
      if (response.data.token) {
        SessionManager.saveSession(response.data.token);
      } else {
        console.warn("Servidor não enviou token!");
      }

      return response.data;
      
    } catch (error) {
      console.error("Erro no Login:", error);
      throw error;
    }
  },

  logout: () => {
    // 1. SessionManager limpa os dados
    SessionManager.clearSession();
    // 2. Redireciona
    window.location.href = "/login";
  },

  getCurrentUser: () => {
    // Busca do SessionManager
    return SessionManager.getUser();
  },

resetPasswordConfirm: (payload) => {
    return api.post(ENDPOINTS.AUTH.RESET_PASSWORD, payload);
  },





  forgotPassword: async (payload) => {
    return await api.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, payload);
  },

 
};



export default AuthService;