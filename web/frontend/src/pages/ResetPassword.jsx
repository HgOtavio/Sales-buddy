import React from "react";
import { useResetPassword } from "../hooks/useResetPassword";

export function ResetForm({ onBack }) {
  // Passamos o onBack como o onSuccess do hook
  const {
    token, setToken,
    newPassword, setNewPassword,
    confirmPassword, setConfirmPassword,
    newPassRef,
    confirmPassRef,
    handleReset,
    isLoading
  } = useResetPassword({ onSuccess: onBack });

  return (
    <>
      <p style={{ color: 'white', marginBottom: '10px', fontSize: '0.9rem', textAlign: 'center' }}>
        COLE O TOKEN RECEBIDO E DEFINA SUA SENHA
      </p>

      <input
        type="text"
        placeholder="COLE O TOKEN AQUI"
        value={token}
        onChange={(e) => setToken(e.target.value)}
        disabled={isLoading}
        onKeyDown={(e) => e.key === "Enter" && newPassRef.current?.focus()}
      />

      <input
        type="password"
        placeholder="NOVA SENHA"
        value={newPassword}
        onChange={(e) => setNewPassword(e.target.value)}
        ref={newPassRef}
        disabled={isLoading}
        onKeyDown={(e) => e.key === "Enter" && confirmPassRef.current?.focus()}
      />

      <input
        type="password"
        placeholder="CONFIRME A NOVA SENHA"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        ref={confirmPassRef}
        disabled={isLoading}
        onKeyDown={(e) => e.key === "Enter" && !isLoading && handleReset()}
      />

      <button onClick={handleReset} disabled={isLoading}>
        {isLoading ? "Salvando..." : "Definir Senha"}
      </button>

      <a href="#" onClick={(e) => { e.preventDefault(); onBack(); }} className="forgot">
        Voltar para o Login
      </a>
    </>
  );
}