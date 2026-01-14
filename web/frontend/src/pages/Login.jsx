import { useLoginLogic } from "../hooks/useLoginLogic";
import { LoginForm } from "../components/LoginForm";
import { ResetForm } from "../components/ResetForm";
import { ToastContainer } from 'react-toastify';
import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import "../styles/login.css";

export default function Login() {
  const { mode, setMode, handleLogin, handleReset } = useLoginLogic();

  return (
    <div className="login-page" style={{ backgroundImage: `url(${background})` }}>
      <img src={logo} className="logo" alt="Logo" />

      <div className="inputs">
        {mode === "login" ? (
          <LoginForm onLogin={handleLogin} onForgot={() => setMode("reset")} />
        ) : (
          <ResetForm onReset={handleReset} onBack={() => setMode("login")} />
        )}
      </div>

      <ToastContainer theme="colored" autoClose={3000} />
    </div>
  );
}