import { useState } from "react";
import LoginDTO from "../dtos/LoginDTO";
import AuthService from "../services/AuthService";
import "../styles/login.css";

import logo from "../assets/logo.svg";
import background from "../assets/background.png";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  function handleLogin() {
    const dto = new LoginDTO(email, password);
    const success = AuthService.login(dto);

    if (!success) {
      alert("Usuário ou senha inválidos");
      return;
    }

    alert("Login mockado com sucesso ");
  }

  return (
    <div
      className="login-page"
      style={{ backgroundImage: `url(${background})` }}
    >
      <img src={logo} className="logo" />


      <div className="inputs">
        <input
          type="text"
          placeholder="Usuário"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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
