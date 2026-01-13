import { useState } from "react";
import { useNavigate } from "react-router-dom";
import AuthService from "../services/AuthService";
import "../styles/login.css";

import logo from "../assets/logo.svg";
import background from "../assets/background.png";

export default function Login() {
  const [user, setUser] = useState("");
  const [password, setPassword] = useState("");
  
  const navigate = useNavigate();

  async function handleLogin() {
    try {
      await AuthService.login(user, password);
      navigate("/dashboard"); 
    } catch (error) {
      console.error(error);
      const msg = error.response?.data?.error || "Usuário ou senha inválidos";
      alert(msg);
    }
  }

  return (
    <div
      className="login-page"
      style={{ backgroundImage: `url(${background})` }}
    >
      <img src={logo} className="logo" alt="Logo" />

      <div className="inputs">
        <input
          type="text"
          placeholder="Usuário"
          value={user}
          onChange={(e) => setUser(e.target.value)}
        />

        <input
          type="password"
          placeholder="Senha"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button onClick={handleLogin}>Entrar</button>

        <a href="#" className="forgot">
          Esqueci minha senha
        </a>
      </div>
    </div>
  );
}