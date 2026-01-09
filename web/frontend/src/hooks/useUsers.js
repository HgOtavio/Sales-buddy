import { useState } from "react";

const initialUsers = [
  { id: 1, usuario: "João", nome: "João Silva", empresa: "Empresa X", cnpj: "12.345.678/0001-90" },
  { id: 2, usuario: "Maria", nome: "Maria Oliveira", empresa: "Empresa Y", cnpj: "98.765.432/0001-10" },
  { id: 3, usuario: "Pedro", nome: "Pedro Santos", empresa: "Empresa Z", cnpj: "11.222.333/0001-00" },
];

export function useUsers() {
  const [users, setUsers] = useState(initialUsers);
  const [selectedIds, setSelectedIds] = useState([]); 
  const [isModalOpen, setIsModalOpen] = useState(false);

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

  function confirmDelete() {
    setUsers((prev) => prev.filter((u) => !selectedIds.includes(u.id)));
    setSelectedIds([]);
    setIsModalOpen(false);
  }

  function cancelDelete() {
    setIsModalOpen(false);
  }

  const selectedNames = users
    .filter((u) => selectedIds.includes(u.id))
    .map((u) => u.nome)
    .join("\n");

  return {
    users,
    selectedIds,
    selectedNames,
    isModalOpen,
    toggleSelection,
    requestDelete,
    confirmDelete,
    cancelDelete
  };
}