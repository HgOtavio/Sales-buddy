import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import { validateLoginInputs } from "../utils/loginValidator";

// Imports da Arquitetura Limpa
import AuthService from "../services/AuthService";
import { toLoginRequest, toResetPasswordRequest } from "../dtos/authDTO";

export function useLoginLogic() {
  const [mode, setMode] = useState("login");  
  const navigate = useNavigate();

  // --- LÓGICA DE LOGIN ---
  const handleLogin = async (user, password) => {
    // 1. Validação de UI (Campos vazios)
    const error = validateLoginInputs(user, password);
    if (error) { toast.error(error); return; }

    try {
      // 2. DTO: Prepara dados
      const payload = toLoginRequest(user, password);

      // 3. SERVICE: Chama API (e salva sessão internamente)
      // Atenção: Certifique-se que seu AuthService.login aceita o objeto payload
      await AuthService.login(payload.user, payload.password); 
      
      navigate("/dashboard");
    } catch (err) {
      toast.error(err.response?.data?.error || "Falha no login");
    }
  };

  // --- LÓGICA DE RESET DE SENHA ---
  const handleReset = async (data) => {
    // 1. Validação de UI
    if (!data.token || !data.newPassword || !data.confirmPassword) {
      toast.error("Preencha todos os campos.");
      return;
    }
    if (data.newPassword !== data.confirmPassword) {
      toast.error("As senhas não coincidem.");
      return;
    }

    try {
      // 2. DTO: Limpa os dados (remove confirmPassword, trim no token)
      const payload = toResetPasswordRequest(data);

      // 3. SERVICE: Envia para a API
      await AuthService.resetPasswordConfirm(payload);
      
      toast.success("Senha definida! Faça login.");
      setMode("login");
      
    } catch (err) {
      toast.error(err.response?.data?.error || "Erro no reset");
    }
  };

  return { mode, setMode, handleLogin, handleReset };
}