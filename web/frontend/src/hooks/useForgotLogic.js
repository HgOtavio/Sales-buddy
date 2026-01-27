import { useState } from "react";
import { toast } from "react-toastify";
import AuthService from "../services/AuthService"; // <--- Importa o Service
import { toForgotPasswordRequest } from "../dtos/authDTO"; // <--- Importa o DTO

export function useForgotLogic({ onSuccess }) {
  const [isLoading, setIsLoading] = useState(false);

  const handleSendEmail = async (email) => {
    if (!email) {
      toast.warning("Por favor, digite seu e-mail.");
      return;
    }

    setIsLoading(true);

    try {
      
      // 1. DTO: Prepara e limpa o dado
      const payload = toForgotPasswordRequest(email);

      // 2. SERVICE: Envia para a API
      const response = await AuthService.forgotPassword(payload);
      
      // 3. HOOK: Sucesso visual
      toast.success(response.data.message || "Token enviado! Verifique sua caixa de entrada.");
      if (onSuccess) onSuccess(); 
      
    } catch (error) {
      // 4. HOOK: Tratamento de erro visual
      const errorMessage = error.response?.data?.message || "E-mail não encontrado ou erro no servidor.";
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    handleSendEmail,
    isLoading
  };
}