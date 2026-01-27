import { useState, useEffect, useCallback } from "react";
import { toast } from 'react-toastify'; 

// Arquitetura Limpa
import { UserService } from "../services/UserService";
import { SessionManager } from "../utils/SessionManager"; // <--- Segurança
import { toUserUI } from "../dtos/userDTO"; // <--- Padronização

export function useUsers() {
  const [users, setUsers] = useState([]); 
  const [selectedIds, setSelectedIds] = useState([]); 
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // --- CARREGAR USUÁRIOS ---
  const refreshUsers = useCallback(async () => {
    try {
      const response = await UserService.getAll();
      // Mapeia os dados brutos para o formato da UI
      const cleanUsers = response.data.map(toUserUI);
      setUsers(cleanUsers);
    } catch (error) {
      console.error("Erro ao carregar usuários:", error);
      toast.error("Erro ao carregar lista de usuários");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshUsers();
  }, [refreshUsers]);

  // --- LÓGICA DE SELEÇÃO ---
  function toggleSelection(id) {
    // Usa SessionManager em vez de localStorage cru
    const currentUser = SessionManager.getUser();
    
    if (currentUser && id === currentUser.id) {
      toast.warning("Você não pode selecionar sua própria conta para exclusão.");
      return;
    }

    if (selectedIds.includes(id)) {
      setSelectedIds((prev) => prev.filter((itemId) => itemId !== id));
    } else {
      setSelectedIds((prev) => [...prev, id]);
    }
  }

  // --- LÓGICA DE DELEÇÃO ---
  function requestDelete() {
    if (selectedIds.length > 0) {
      setIsModalOpen(true);
    }
  }

  async function confirmDelete() {
    try {
      const currentUser = SessionManager.getUser();

      // Dupla checagem de segurança
      if (currentUser && selectedIds.includes(currentUser.id)) {
        toast.error("Ação bloqueada: Sua conta está na lista de exclusão.");
        setIsModalOpen(false);
        return;
      }
      
      // Chama o Service para cada ID
      // O Hook gerencia a Promise.all, mas quem chama a API é o Service
      await Promise.all(selectedIds.map(id => UserService.delete(id)));

      toast.success("Usuário(s) excluído(s) com sucesso!");
      setSelectedIds([]);
      setIsModalOpen(false);
      refreshUsers(); 

    } catch (error) {
       console.error(error);
       const msg = error.response?.data?.error || "Erro ao excluir. Tente novamente.";
       toast.error(msg);
       setIsModalOpen(false);
    }
  }

  function cancelDelete() {
    setIsModalOpen(false);
  }

  // Helper de visualização (View Logic simples é aceitável no Hook ou direto no Componente)
  const selectedNames = users
    .filter((u) => selectedIds.includes(u.id))
    .map((u) => u.name)
    .join("\n");

  return {
    users,
    loading,
    selectedIds,
    selectedNames,
    isModalOpen,
    toggleSelection,
    requestDelete,
    confirmDelete,
    cancelDelete,
    refreshUsers 
  };
}