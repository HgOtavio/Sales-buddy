import { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/login.css"; // Reaproveita o CSS do login
import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import { toast, ToastContainer } from 'react-toastify';

export default function ResetPassword() {
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
      const response = await api.post('/auth/reset-password', {
        token,
        newPassword,
        confirmPassword
      });

      toast.success(response.data.message || "Senha definida com sucesso!");
      
      setTimeout(() => navigate("/login"), 2000);

    } catch (error) {
      const msg = error.response?.data?.error || "Erro ao resetar senha.";
      toast.error(msg);
    }
  }

  return (
    <div className="login-page" style={{ backgroundImage: `url(${background})` }}>
      <img src={logo} className="logo" alt="Logo" />

      <div className="inputs">
        <p style={{ color: 'white', marginBottom: '10px', fontSize: '0.9rem' }}>
          COLE O TOKEN RECEBIDO E DEFINA SUA SENHA
        </p>

        <input
          type="text"
          placeholder="COLE O TOKEN AQUI"
          value={token}
          onChange={(e) => setToken(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && newPassRef.current?.focus()}
        />

        <input
          type="password"
          placeholder="NOVA SENHA"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          ref={newPassRef}
          onKeyDown={(e) => e.key === "Enter" && confirmPassRef.current?.focus()}
        />

        <input
          type="password"
          placeholder="CONFIRME A NOVA SENHA"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          ref={confirmPassRef}
          onKeyDown={(e) => e.key === "Enter" && handleReset()}
        />

        <button onClick={handleReset}>Definir Senha</button>

        <a href="#" onClick={() => navigate("/login")} className="forgot">
          Voltar para o Login
        </a>
      </div>

      <ToastContainer theme="colored" />
    </div>
  );
}