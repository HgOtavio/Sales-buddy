import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from 'react-toastify';
import api from "../services/api";
import { ENDPOINTS } from "../services/endpoints";

export function useResetPassword() {
  const [token, setToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  
  const newPassRef = useRef(null);
  const confirmPassRef = useRef(null);
  const navigate = useNavigate();

  async function handleReset() {
    if (!token || !newPassword || !confirmPassword) {
      toast.error("Preencha todos os campos.");
      return;
    }

    if (newPassword !== confirmPassword) {
      toast.error("As senhas não coincidem.");
      return;
    }

    try {
      const response = await api.post(ENDPOINTS.AUTH.RESET_PASSWORD, {
        token,
        newPassword,
        confirmPassword
      });

      toast.success(response.data.message || "Senha definida com sucesso!");
      

    } catch (error) {
      const msg = error.response?.data?.error || "Erro ao resetar senha.";
      toast.error(msg);
    }
  }

  return {
    token, setToken,
    newPassword, setNewPassword,
    confirmPassword, setConfirmPassword,
    newPassRef,
    confirmPassRef,
    handleReset,
    navigate 
  };
}