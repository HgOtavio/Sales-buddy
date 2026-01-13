import { useState, useEffect, useCallback } from "react";
import { toast } from 'react-toastify'; 
import api from "../services/api";

export function useUsers() {
  const [users, setUsers] = useState([]); 
  const [selectedIds, setSelectedIds] = useState([]); 
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  // Busca usuários no 
  const refreshUsers = useCallback(async () => {
    try {
      const token = localStorage.getItem('salesToken');
      if (!token) return;

      const response = await api.get('/auth/users', {
        headers: { Authorization: `Bearer ${token}` }
      });

      setUsers(response.data);
    } catch (error) {
      console.error("Erro ao carregar usuários:", error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshUsers();
  }, [refreshUsers]);

  function toggleSelection(id) {
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
        const token = localStorage.getItem('salesToken');
        const config = { headers: { Authorization: `Bearer ${token}` } };

       
        await Promise.all(selectedIds.map(id => 
            api.delete(`/auth/users/${id}`, config)
        ));

        toast.success("Usuário(s) excluído(s) com sucesso!");
        
        setSelectedIds([]);
        setIsModalOpen(false);
        
        refreshUsers(); 

    } catch (error) {
        console.error(error);
        toast.error("Erro ao excluir. Tente novamente.");
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