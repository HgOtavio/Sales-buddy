import { useLoginLogic } from "../hooks/useLoginLogic";
import { LoginForm } from "../components/LoginForm";
import { ForgotForm } from "../components/ForgotForm"; 
import { ResetForm } from "../components/ResetForm";
import { ToastContainer } from 'react-toastify';
import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import "../styles/login.css";

export default function Login() {
  const { mode, setMode, handleLogin } = useLoginLogic();

  const renderForm = () => {
    switch (mode) {
      case "login":
        return (
          <LoginForm 
            onLogin={handleLogin} 
            onForgot={() => setMode("forgot")}
          />
        );
      case "forgot":
        return (
          <ForgotForm 
            onBack={() => setMode("login")} 
            onHaveToken={() => setMode("reset")}
          />
        );
      case "reset":
        return (
          <ResetForm 
            onBack={() => setMode("login")} 
          />
        );
      default:
        return null;
    }
  };

  return (
    <div className="login-page" style={{ backgroundImage: `url(${background})` }}>
      <img src={logo} className="logo" alt="Logo" />

      <div className="inputs">
        {renderForm()}
      </div>

      <ToastContainer theme="colored" autoClose={3000} />
    </div>
  );
}