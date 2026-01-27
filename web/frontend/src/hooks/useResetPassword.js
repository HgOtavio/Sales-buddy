import { useState, useRef } from "react";
import { toast } from 'react-toastify';
import AuthService from "../services/AuthService"; // <--- Service
import { toResetPasswordRequest } from "../dtos/authDTO"; // <--- DTO

export function useResetPassword({ onSuccess }) {
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false); 
  
  const newPassRef = useRef(null);
  const confirmPassRef = useRef(null);

  async function handleReset() {
    // 1. Validação de UI
    if (!token || !newPassword || !confirmPassword) {
      toast.warning("Preencha todos os campos.");
      return;
    }

    if (newPassword !== confirmPassword) {
      toast.error("As senhas não coincidem.");
      return;
    }

    setIsLoading(true);

    try {
      // 2. DTO: Prepara os dados (limpa token e remove confirmPassword)
      const payload = toResetPasswordRequest(token, newPassword);

      // 3. SERVICE: Prepara a chamada (mas não espera ainda, pq o toast.promise precisa da Promise)
      const apiCallPromise = AuthService.resetPasswordConfirm(payload);

      // 4. UI: Gerencia o feedback com Toast Promise
      await toast.promise(
        apiCallPromise,
        {
          pending: "Redefinindo senha...",
          success: "Senha definida com sucesso! Faça seu login.",
          error: {
            render({ data }) {
              // Pega a mensagem de erro que vem do axios
              return data.response?.data?.error || "Erro ao resetar senha.";
            }
          }
        },
        { autoClose: 2000 }
      );

      // 5. Limpeza pós-sucesso
      setToken("");
      setNewPassword("");
      setConfirmPassword("");
      if (onSuccess) onSuccess(); 

    } catch (error) {
      console.error("Erro no fluxo de reset:", error);
      // O toast.promise já exibiu o erro visualmente, aqui é só log
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