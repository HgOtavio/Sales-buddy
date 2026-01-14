import { useState, useRef } from "react";

export function ResetForm({ onReset, onBack }) {
  const [data, setData] = useState({ token: "", newPassword: "", confirmPassword: "" });
  const passRef = useRef(null);
  const confirmRef = useRef(null);

  const handleChange = (e) => setData({ ...data, [e.target.name]: e.target.value });

  return (
    <>
      <input 
        name="token"
        placeholder="COLE O TOKEN RECEBIDO" 
        value={data.token} 
        onChange={handleChange}
        onKeyDown={e => e.key === "Enter" && passRef.current?.focus()}
      />
      <input 
        name="newPassword"
        type="password" 
        placeholder="NOVA SENHA" 
        ref={passRef}
        value={data.newPassword} 
        onChange={handleChange}
        onKeyDown={e => e.key === "Enter" && confirmRef.current?.focus()}
      />
      <input 
        name="confirmPassword"
        type="password" 
        placeholder="CONFIRME A SENHA" 
        ref={confirmRef}
        value={data.confirmPassword} 
        onChange={handleChange}
        onKeyDown={e => e.key === "Enter" && onReset(data)}
      />
      <button onClick={() => onReset(data)}>Definir Senha</button>
      <a href="#" className="forgot" onClick={e => { e.preventDefault(); onBack(); }}>
        Voltar para o Login
      </a>
    </>
  );
}