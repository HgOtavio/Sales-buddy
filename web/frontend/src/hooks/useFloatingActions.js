import { toast } from 'react-toastify';
import api from "../services/api";
import { ENDPOINTS } from "../services/endpoints"; // <--- Importando as rotas

export function useFloatingActions(setActive, refreshUsers, editingUser, activeTab) {
  
  function handleGoToAdd() {
    if (activeTab === "cadastro") return;
    setActive("cadastro");
  }

  async function handleFormSubmit(formData) {
    try {
      

      if (editingUser) {
        const payload = { ...formData };

        delete payload.company; 
        delete payload.taxId;
        delete payload.id;        
        delete payload.createdAt; 
        delete payload.updatedAt;
        delete payload.password;  

        await api.put(`${ENDPOINTS.AUTH.USERS}/${formData.id}`, payload);
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

    const confirm = window.confirm(`Deseja enviar um token para ${editingUser.email}?`);
    
    if (confirm) {
      try {
        console.log("Enviando solicitação de reset...");
        
        const response = await api.post(ENDPOINTS.AUTH.FORGOT_PASSWORD, { 
            email: editingUser.email 
        });
        
        console.log("Resposta do servidor:", response.data);
        toast.success("Token enviado!");
      } catch (error) {
        console.error("Erro completo capturado no Front:", error.response || error);
        
        const msg = error.response?.data?.message || error.response?.data?.error || "Erro ao solicitar reset";
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