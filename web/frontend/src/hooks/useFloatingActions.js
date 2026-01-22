import { toast } from 'react-toastify';
import api from "../services/api";
import { ENDPOINTS } from "../services/endpoints"; 

export function useFloatingActions(setActive, refreshUsers, editingUser, activeTab) {
  
  function handleGoToAdd() {
    if (activeTab === "cadastro") return;
    setActive("cadastro");
  }

  async function handleFormSubmit(formData) {
    try {
      
      if (editingUser) {
        const payload = {
            id: formData.id,  
            name: formData.name,
            email: formData.email,
            user: formData.user || formData.username,
        };

        await api.put(ENDPOINTS.AUTH.USERS, payload);
        toast.success("Usuário atualizado com sucesso!");

      } else {
        const payload = {
            name: formData.name,
            email: formData.email,
            password: formData.password,
            company: formData.company,
            user: formData.user || formData.username, 
            taxId: formData.taxId || formData.cnpj
        };

        await api.post(ENDPOINTS.AUTH.REGISTER, payload);
        toast.success("Usuário cadastrado com sucesso! Verifique o e-mail.");
      }

      setActive("usuarios");
      refreshUsers();

    } catch (error) {
      const title = error.response?.data?.error || "Erro";
      const detail = error.response?.data?.message || "";
      
      toast.error(detail ? `${title}: ${detail}` : title);
      console.error("Erro na requisição:", error.response?.data);
    }
  }

 
  async function handleResetPassword() {
    if (!editingUser?.email) {
      toast.error("E-mail do usuário não identificado.");
      return;
    }

    try {
     
      console.log("Enviando solicitação de reset...");
      
      await api.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, { 
          email: editingUser.email 
      });
      
     
      toast.success("E-mail de recuperação enviado com sucesso!");

    } catch (error) {
      console.error("Erro ao resetar senha:", error.response || error);
      const msg = error.response?.data?.message || "Erro ao solicitar reset.";
      toast.error(msg);
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