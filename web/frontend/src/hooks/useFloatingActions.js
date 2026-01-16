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
        // --- LÓGICA DE EDIÇÃO (PUT) ---
        // CRIAMOS UM NOVO OBJETO PARA LIMPAR DADOS PROIBIDOS
        // Copia os dados do form
        const payload = { ...formData };

        // REMOVE campos que o backend bloqueia (evita erro 403)
        delete payload.company; 
        delete payload.taxId;
        delete payload.id;        // O ID já vai na URL
        delete payload.createdAt; // Metadados não devem ser enviados
        delete payload.updatedAt;
        delete payload.password;  // Geralmente não se edita senha nessa rota (opcional)

        // Envia apenas o que pode ser alterado (name, user, email)
        await api.put(`/auth/users/${formData.id}`, payload, config);
        toast.success("Usuário atualizado com sucesso!");

      } else {
        // --- LÓGICA DE CADASTRO (POST) ---
        // GARANTIMOS O NOME DAS CHAVES QUE O BACKEND ESPERA
        const payload = {
            name: formData.name,
            email: formData.email,
            password: formData.password,
            company: formData.company,
            // Verifica se o form usa 'user' ou 'username' e manda 'user'
            user: formData.user || formData.username, 
            // Verifica se o form usa 'taxId' ou 'cnpj' e manda 'taxId'
            taxId: formData.taxId || formData.cnpj
        };

        await api.post('/auth/register', payload, config);
        toast.success("Usuário cadastrado com sucesso! Verifique o e-mail.");
      }

      setActive("usuarios");
      refreshUsers();

    } catch (error) {
      // MELHORIA NA EXIBIÇÃO DO ERRO
      // O backend agora manda { error: "Titulo", message: "Detalhe" }
      // Tentamos pegar a mensagem detalhada primeiro
      const title = error.response?.data?.error || "Erro";
      const detail = error.response?.data?.message || "";
      
      // Exibe: "Dados Incompletos: O campo email é obrigatório"
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
        console.log("Enviando POST para:", "/auth/forgot-password", "com dados:", { email: editingUser.email });
        
        const response = await api.post('/auth/forgot-password', { email: editingUser.email });
        
        console.log("Resposta do servidor:", response.data);
        toast.success("Token enviado!");
      } catch (error) {
        console.error("Erro completo capturado no Front:", error.response || error);
        
        // Ajuste aqui também para pegar a mensagem melhor
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