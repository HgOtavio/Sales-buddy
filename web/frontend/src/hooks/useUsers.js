import { useState, useEffect, useCallback } from "react";
import { toast } from 'react-toastify'; 
import api from "../services/api";
import { ENDPOINTS } from "../services/endpoints"; 

export function useUsers() {
  const [users, setUsers] = useState([]); 
  const [selectedIds, setSelectedIds] = useState([]); 
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  const refreshUsers = useCallback(async () => {
    try {
      const response = await api.get(ENDPOINTS.AUTH.USERS);
      setUsers(response.data);
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

  function toggleSelection(id) {
    const userData = JSON.parse(localStorage.getItem('userData'));
    const meuId = userData?.id;

    if (id === meuId) {
      toast.warning("Você não pode selecionar sua própria conta para exclusão.");
      return;
    }

    if (selectedIds.includes(id)) {
      setSelectedIds((prev) => prev.filter((itemId) => itemId !== id));
    } else {
      setSelectedIds((prev) => [...prev, id]);
    }
  }

  function requestDelete() {
    if (selectedIds.length > 0) {
      setIsModalOpen(true);
    }
  }

  async function confirmDelete() {
    try {
      const userData = JSON.parse(localStorage.getItem('userData'));
      const meuId = userData?.id;

      if (selectedIds.includes(meuId)) {
        toast.error("Ação bloqueada: Sua conta está na lista de exclusão.");
        setIsModalOpen(false);
        return;
      }

      
      await Promise.all(selectedIds.map(id => 
          api.delete(ENDPOINTS.AUTH.USERS, {
              data: { id: id } 
          })
      ));

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