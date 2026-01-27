import { useState } from "react";
import { useForgotLogic } from "../../hooks/useForgotLogic"; 

export function ForgotForm({ onBack, onHaveToken }) {
  const [email, setEmail] = useState("");

  const { handleSendEmail, isLoading } = useForgotLogic({ 
    onSuccess: onHaveToken 
  });

  return (
    <>
      <input
        name="email"
        type="email"
        placeholder="DIGITE SEU E-MAIL"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        disabled={isLoading} 
        onKeyDown={(e) => e.key === "Enter" && !isLoading && handleSendEmail(email)}
      />

      <button 
        onClick={() => handleSendEmail(email)} 
        disabled={isLoading}
      >
        {isLoading ? "Enviando..." : "Enviar Token"}
      </button>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '10px' }}>
        <a href="#" className="forgot" onClick={(e) => { e.preventDefault(); onHaveToken(); }}>
          Já tem o token?
        </a>
        <a href="#" className="forgot" onClick={(e) => { e.preventDefault(); onBack(); }}>
          Voltar para o Login
        </a>
      </div>
    </>
  );
}