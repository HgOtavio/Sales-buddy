import { useState, useRef } from "react";
import { toast } from 'react-toastify';
import api from "../services/api"; 
import { ENDPOINTS } from "../services/endpoints"; 

export function useResetPassword({ onSuccess }) {
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false); 
  
  const newPassRef = useRef(null);
  const confirmPassRef = useRef(null);

  async function handleReset() {
    if (!token || !newPassword || !confirmPassword) {
      toast.warning("Preencha todos os campos.");
      return;
    }

    if (newPassword !== confirmPassword) {
      toast.error("As senhas não coincidem.");
      return;
    }

    setIsLoading(true);

    const request = api.post(ENDPOINTS.AUTH.RESET_PASSWORD, {
      token,
      newPassword,
      confirmPassword
    });

    try {
      await toast.promise(
        request,
        {
          pending: "Redefinindo senha...",
          success: "Senha definida com sucesso! Faça seu login.",
          error: {
            render({ data }) {
              return data.response?.data?.error || "Erro ao resetar senha.";
            }
          }
        },
        { autoClose: 2000 }
      );

      setToken("");
      setNewPassword("");
      setConfirmPassword("");
      onSuccess(); 

    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  }

  return {
    token, setToken,
    newPassword, setNewPassword,
    confirmPassword, setConfirmPassword,
    newPassRef,
    confirmPassRef,
    handleReset,
    isLoading
  };
}