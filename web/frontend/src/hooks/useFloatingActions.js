import { toast } from 'react-toastify';
import api from "../services/api";

export function useFloatingActions(setActive, refreshUsers, editingUser, activeTab) {
  
  function handleGoToAdd() {
    if (activeTab === "cadastro") return;
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
        toast.success("Usuário cadastrado com sucesso! Verifique o e-mail.");
      }

      setActive("usuarios");
      refreshUsers();

    } catch (error) {
      const msg = error.response?.data?.error ;
      toast.error(msg);
    }
  }

  async function handleResetPassword() {
    if (!editingUser?.email) {
      toast.error("E-mail do usuário não identificado.");
      return;
    }

    const confirm = window.confirm(`Deseja enviar um token para ${editingUser.email}?`);
    
    if (confirm) {
      try {
        console.log("Enviando POST para:", "/auth/forgot-password", "com dados:", { email: editingUser.email });
        
        const response = await api.post('/auth/forgot-password', { email: editingUser.email });
        
        console.log("Resposta do servidor:", response.data);
        toast.success("Token enviado!");
      } catch (error) {
        console.error("Erro completo capturado no Front:", error.response || error);
        const msg = error.response?.data?.error || "Erro ao solicitar reset";
        toast.error(msg);
      }
    }
  }

  
  const isAddDisabled = activeTab === "cadastro";

  return {
    handleGoToAdd,
    handleFormSubmit,   
    handleResetPassword,
    isAddDisabled 
  };
}