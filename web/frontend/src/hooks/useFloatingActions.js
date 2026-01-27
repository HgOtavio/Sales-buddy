import { toast } from 'react-toastify';
import { UserService } from "../services/UserService"; // <--- Usa o Service
import { toCreateUserRequest, toUpdateUserRequest } from "../dtos/userDTO"; // <--- Usa o Mapper

export function useFloatingActions(setActive, refreshUsers, editingUser, activeTab) {
  
  function handleGoToAdd() {
    if (activeTab === "cadastro") return;
    setActive("cadastro");
  }

  async function handleFormSubmit(formData) {
    try {
      
      if (editingUser) {
        // 1. Usa o DTO para limpar os dados
        const payload = toUpdateUserRequest(formData);
        
        // 2. Chama o Service
        await UserService.update(payload);
        toast.success("Usuário atualizado com sucesso!");

      } else {
        // 1. Usa o DTO para limpar os dados
        const payload = toCreateUserRequest(formData);
        
        // 2. Chama o Service
        await UserService.register(payload);
        toast.success("Usuário cadastrado com sucesso! Verifique o e-mail.");
      }

      setActive("usuarios");
      refreshUsers();

    } catch (error) {
      // Dica: Você pode criar um utils/errorHandler.js para limpar isso também
      const title = error.response?.data?.error || "Erro";
      const detail = error.response?.data?.message || "";
      toast.error(detail ? `${title}: ${detail}` : title);
    }
  }

  async function handleResetPassword() {
    if (!editingUser?.email) {
      toast.error("E-mail do usuário não identificado.");
      return;
    }

    try {
      // Chamada limpa via Service
      await UserService.resetPassword(editingUser.email);
      toast.success("E-mail de recuperação enviado com sucesso!");

    } catch (error) {
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