import React from "react";
import { useResetPassword } from "../hooks/useResetPassword"; // <--- Importa o Hook criado
import "../styles/login.css"; 
import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import { ToastContainer } from 'react-toastify';

export default function ResetPassword() {
  const {
    token, setToken,
    newPassword, setNewPassword,
    confirmPassword, setConfirmPassword,
    newPassRef,
    confirmPassRef,
    handleReset,
    navigate
  } = useResetPassword();

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

        <a href="#" onClick={(e) => { e.preventDefault(); navigate("/login"); }} className="forgot">
          Voltar para o Login
        </a>
      </div>

      <ToastContainer theme="colored" />
    </div>
  );
}