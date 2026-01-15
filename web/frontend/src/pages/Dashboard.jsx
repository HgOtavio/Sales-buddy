import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

import { useUsers } from "../hooks/useUsers";
import { useFloatingActions } from "../hooks/useFloatingActions"; 
import api from "../services/api"; 

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { UsersTable } from "../components/UsersTable";
import { SalesTable } from "../components/SalesTable"; 
import { AddUserForm } from "../components/AddUserForm"; 
import { SidebarItem } from "../components/SidebarItem";
import { FloatingButtons } from "../components/FloatingButtons";
import { ConfirmationModal } from "../components/ConfirmationModal";

import logo from "../assets/logo.svg";
import usersIcon from "../assets/icon-users.svg";
import salesIcon from "../assets/icon-sales.svg";
import logoutIcon from "../assets/icon-logout.svg";
import usersIconBlue from "../assets/icon-users-blue.svg";
import salesIconBlue from "../assets/icon-sales-blue.svg";
import background from "../assets/background.png";

export default function Dashboard() {
  // 1. Definição de Estados (SEMPRE NO TOPO)
  const [active, setActive] = useState("usuarios"); 
  const [editingUser, setEditingUser] = useState(null); 
  const [receiptUrl, setReceiptUrl] = useState(null);
  const [isLoading, setIsLoading] = useState(true); 
  
  const navigate = useNavigate();

  // 2. Hooks Customizados (MOVIDOS PARA CIMA - ANTES DO RETURN)
  // Eles precisam existir mesmo que o loading esteja true
  const { 
    users, 
    selectedIds,
    selectedNames,
    isModalOpen,
    toggleSelection, 
    requestDelete, 
    confirmDelete, 
    cancelDelete,
    refreshUsers 
  } = useUsers();

  const { 
    handleGoToAdd, 
    handleFormSubmit, 
    handleResetPassword,
    isAddDisabled 
  } = useFloatingActions(setActive, refreshUsers, editingUser, active);

  // 3. Funções Auxiliares
  const performLogout = () => {
    localStorage.removeItem('salesToken'); 
    localStorage.removeItem('userData');  
    navigate('/login');     
  };

  // 4. Effects
  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('salesToken');
      
      if (!token) {
        performLogout();
        return;
      }

      try {
        await api.get('/auth/verify', {
            headers: { Authorization: `Bearer ${token}` }
        });

        setIsLoading(false);

      } catch {
        performLogout(); 
      }
    };

    checkAuth();
  }, [navigate]);

  const handleEditUser = (user) => {
    setEditingUser(user); 
    setActive("cadastro"); 
  };

  const handleOpenNewUser = () => {
    setEditingUser(null); 
    handleGoToAdd(); 
  };

  const handleViewReceipt = (url) => {
    setReceiptUrl(url); 
  };

  if (isLoading) {
    return (
      <div style={{ 
        height: "100vh", 
        display: "flex", 
        justifyContent: "center", 
        alignItems: "center", 
        background: "#f0f2f5",
        flexDirection: "column"
      }}>
        <h2>Verificando credenciais...</h2>
        <p>Aguarde um momento.</p>
      </div>
    );
  }

  // 6. Renderização Principal
  return (
    <div className="dashboard" style={{ backgroundImage: `url(${background})` }}>      
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
          onClick={performLogout} 
        />
      </aside>

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
            <SalesTable onViewReceipt={handleViewReceipt} />
          )}

          {active === "cadastro" && (
            <AddUserForm 
                userToEdit={editingUser} 
                onSubmit={handleFormSubmit} 
                formId="user-form-id"
            />
          )}

        </div>

        <FloatingButtons 
          activeTab={active} 
          onDelete={requestDelete}
          disabled={selectedIds.length === 0}
          onAdd={handleOpenNewUser}
          onReset={handleResetPassword} 
          isEditing={!!editingUser}
          isAdding={isAddDisabled} 
          formId="user-form-id"
        />
        
      </main>
      
      <ConfirmationModal 
          isOpen={isModalOpen}
          onClose={cancelDelete}
          onConfirm={confirmDelete}
          userName={selectedNames} 
          variant="delete" 
      />

      <ConfirmationModal 
          isOpen={!!receiptUrl} 
          onClose={() => setReceiptUrl(null)} 
          variant="receipt"
          receiptImageSrc={receiptUrl}
      />

      <ToastContainer 
        position="top-right"
        autoClose={3000} 
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="colored" 
      />

    </div>
  );
}