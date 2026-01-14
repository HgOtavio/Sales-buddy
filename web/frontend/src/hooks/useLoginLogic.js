import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthService from "../services/AuthService";
import api from "../services/api";
import { toast } from 'react-toastify';
import { validateLoginInputs } from "../utils/loginValidator";

export function useLoginLogic() {
  const [mode, setMode] = useState("login"); // "login" ou "reset"
  const navigate = useNavigate();

  const handleLogin = async (user, password) => {
    const error = validateLoginInputs(user, password);
    if (error) { toast.error(error); return; }

    try {
      await AuthService.login(user, password);
      navigate("/dashboard");
    } catch (err) {
      toast.error(err.response?.data?.error || "Falha no login");
    }
  };

  const handleReset = async (data) => {
    if (!data.token || !data.newPassword || !data.confirmPassword) {
      toast.error("Preencha todos os campos.");
      return;
    }
    if (data.newPassword !== data.confirmPassword) {
      toast.error("As senhas não coincidem.");
      return;
    }

    try {
      await api.post('/auth/reset-password', data);
      toast.success("Senha definida! Faça login.");
      setMode("login");
    } catch (err) {
      toast.error(err.response?.data?.error || "Erro no reset");
    }
  };

  return { mode, setMode, handleLogin, handleReset };
}