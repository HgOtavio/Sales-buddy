import { useState } from "react";
import "../styles/dashboard.css";

import { useUsers } from "../hooks/useUsers";
import { useFloatingActions } from "../hooks/useFloatingActions"; 

import { UsersTable } from "../components/UsersTable";
import { SalesTable } from "../components/SalesTable"; 
import { AddUserForm } from "../components/AddUserForm"; 
import { SidebarItem } from "../components/SidebarItem";
import { FloatingButtons } from "../components/FloatingButtons";
import { ConfirmationModal } from "../components/ConfirmationModal";

import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import usersIcon from "../assets/icon-users.svg";
import salesIcon from "../assets/icon-sales.svg";
import logoutIcon from "../assets/icon-logout.svg";
import usersIconBlue from "../assets/icon-users-blue.svg";
import salesIconBlue from "../assets/icon-sales-blue.svg";

export default function Dashboard() {
  // --- Estados da Tela ---
  const [active, setActive] = useState("usuarios"); // Controla qual aba está visível
  const [editingUser, setEditingUser] = useState(null); // Guarda usuário sendo editado
  const [receiptUrl, setReceiptUrl] = useState(null); // Guarda a URL do comprovante (null = modal fechado)

  // --- Hooks Customizados ---
  const { 
    users, 
    selectedIds, 
    selectedNames,
    isModalOpen,
    toggleSelection, 
    requestDelete, 
    confirmDelete, 
    cancelDelete
  } = useUsers();

  const { 
    handleGoToAdd, 
    handleSave, 
    handleResetPassword 
  } = useFloatingActions(setActive);

  // --- Funções de Controle ---

  const handleEditUser = (user) => {
    setEditingUser(user); 
    setActive("cadastro"); 
  };

  const handleOpenNewUser = () => {
    setEditingUser(null); 
    handleGoToAdd(); 
  };

  // Função chamada pela SalesTable quando clica no ícone
  const handleViewReceipt = (url) => {
    setReceiptUrl(url); // Isso faz o modal abrir
  };

  const handleFormSubmit = (formData) => {
    if (editingUser) {
        // Lógica de editar
    } else {
        // Lógica de criar
    }
    setActive("usuarios"); 
  };

  return (
    <div className="dashboard" style={{ backgroundImage: `url(${background})` }}>
      
      {/* Barra Lateral */}
      <aside className="sidebar">
        <img src={logo} className="sidebar-logo" alt="Logo" />
        
        <SidebarItem 
          label="Usuários"
          icon={usersIcon}
          activeIcon={usersIconBlue}
          isActive={active === "usuarios" || active === "cadastro"}
          onClick={() => setActive("usuarios")}
        />

        <SidebarItem 
          label="Vendas"
          icon={salesIcon}
          activeIcon={salesIconBlue}
          isActive={active === "vendas"}
          onClick={() => setActive("vendas")}
        />

        <SidebarItem 
          label="Logout"
          icon={logoutIcon}
          isActive={false}
          onClick={() => alert("Saindo...")} 
        />
      </aside>

      {/* Conteúdo Principal */}
      <main className="content">
        <div className="table-container">
          
          {active === "usuarios" && (
            <UsersTable 
              users={users} 
              selectedIds={selectedIds} 
              onSelect={toggleSelection}
              onEdit={handleEditUser} 
            />
          )}

          {active === "vendas" && (
            <SalesTable 
              onViewReceipt={handleViewReceipt} 
            />
          )}

          {active === "cadastro" && (
            <AddUserForm 
                userToEdit={editingUser} 
                onSubmit={handleFormSubmit}
                onCancel={() => setActive("usuarios")}
            />
          )}

        </div>

        {/* Botões Flutuantes */}
        <FloatingButtons 
          activeTab={active} 
          onDelete={requestDelete}
          disabled={selectedIds.length === 0}
          onAdd={handleOpenNewUser}
          onSave={handleSave}
          onReset={handleResetPassword} 
          isEditing={!!editingUser}
        />
        
      </main>
      
      {/* Modal de Exclusão */}
      <ConfirmationModal 
          isOpen={isModalOpen}
          onClose={cancelDelete}
          onConfirm={confirmDelete}
          userName={selectedNames} 
          variant="delete" 
      />

      {/* Modal de Comprovante */}
      <ConfirmationModal 
          isOpen={!!receiptUrl} // Abre se tiver URL
          onClose={() => setReceiptUrl(null)} // Fecha limpando a URL
          variant="receipt"
          receiptImageSrc={receiptUrl}
      />

    </div>
  );
}