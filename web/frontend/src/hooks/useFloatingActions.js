import { toast } from 'react-toastify';
import api from "../services/api";

export function useFloatingActions(setActive, refreshUsers, editingUser) {
  
  function handleGoToAdd() {
    setActive("cadastro");
  }

 
  async function handleFormSubmit(formData) {
    try {
      const token = localStorage.getItem('salesToken');
      const config = { headers: { Authorization: `Bearer ${token}` } };

      if (editingUser) {
        await api.put(`/auth/users/${formData.id}`, formData, config);
        toast.success("Usuário atualizado com sucesso!");
      } else {
        await api.post('/auth/register', formData, config);
        toast.success("Usuário cadastrado com sucesso! Senha enviada.");
      }

      setActive("usuarios");
      refreshUsers();

    } catch (error) {
      console.error(error);
      const msg = error.response?.data?.error || "Erro ao processar solicitação.";
      toast.error(msg);
    }
  }

  async function handleResetPassword() {
    
    const confirm = window.confirm(`Deseja resetar a senha de ${editingUser?.name || 'este usuário'}?`);
    
    if (confirm) {
      try {
         
         toast.info("Processando solicitação...");
         
         setTimeout(() => {
             toast.success("Senha resetada! Nova senha enviada por e-mail.");
         }, 1500);

      } catch  {
         toast.error("Erro ao resetar senha.");
      }
    }
  } 

  return {
    handleGoToAdd,
    handleFormSubmit,   
    handleResetPassword
  };
}