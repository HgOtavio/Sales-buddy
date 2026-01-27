import { useState, useRef } from "react";

export function LoginForm({ onLogin, onForgot }) {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");
  const passwordRef = useRef(null);

  return (
    <>
      <input 
        placeholder="USUÁRIO" 
        value={user} 
        onChange={e => setUser(e.target.value)}
        onKeyDown={e => e.key === "Enter" && passwordRef.current?.focus()}
      />
      <input 
        type="password" 
        placeholder="SENHA" 
        ref={passwordRef}
        value={password} 
        onChange={e => setPassword(e.target.value)}
        onKeyDown={e => e.key === "Enter" && onLogin(user, password)}
      />
      <button onClick={() => onLogin(user, password)}>Entrar</button>
      <a href="#" className="forgot" onClick={e => { e.preventDefault(); onForgot(); }}>
        Esqueci minha senha
      </a>
    </>
  );
}