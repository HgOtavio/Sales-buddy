import { useState } from "react";
import { toast } from "react-toastify";
import api from "../services/api"; 
import { ENDPOINTS } from "../services/endpoints"; 

export function useForgotLogic({ onSuccess }) {
  const [isLoading, setIsLoading] = useState(false);

  const handleSendEmail = async (email) => {
    if (!email) {
      toast.warning("Por favor, digite seu e-mail.");
      return;
    }

    setIsLoading(true);

    try {
     
      const response = await api.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, { email });

     
      toast.success(response.data.message || "Token enviado! Verifique sua caixa de entrada.");
      onSuccess(); 
      
    } catch (error) {
   
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