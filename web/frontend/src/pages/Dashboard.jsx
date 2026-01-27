import { useState, useEffect, useCallback } from "react"; 
import { useNavigate } from "react-router-dom";
import "../styles/dashboard.css";

import { useUsers } from "../hooks/useUsers";
import { useFloatingActions } from "../hooks/useFloatingActions"; 
import api from "../services/api"; 
import { ENDPOINTS } from "../services/endpoints"; 

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import { UsersTable } from "../components/tables/UsersTable";
import { SalesTable } from "../components/tables/salesTable"; 
import { AddUserForm } from "../components/forms/AddUserForm"; 
import { SidebarItem } from "../components/layout/SidebarItem";
import { FloatingButtons } from "../components/common/FloatingButtons";
import { ConfirmationModal } from "../components/common/ConfirmationModal";

import logo from "../assets/logo.svg";
import usersIcon from "../assets/icon-users.svg";
import salesIcon from "../assets/icon-sales.svg";
import logoutIcon from "../assets/icon-logout.svg";
import usersIconBlue from "../assets/icon-users-blue.svg";
import salesIconBlue from "../assets/icon-sales-blue.svg";
import background from "../assets/background.png";

export default function Dashboard() {
  const [active, setActive] = useState("usuarios"); 
  const [editingUser, setEditingUser] = useState(null); 
  const [selectedSale, setSelectedSale] = useState(null);
  const [isLoading, setIsLoading] = useState(true); 
  
  const navigate = useNavigate();

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

  const performLogout = useCallback(() => {
    localStorage.removeItem('salesToken'); 
    localStorage.removeItem('userData');  
    navigate('/login');     
  }, [navigate]);

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('salesToken');
      
      if (!token) {
        performLogout();
        return;
      }

      try {
        await api.get(ENDPOINTS.AUTH.VERIFY); 
        setIsLoading(false);
      } catch {
        performLogout(); 
      }
    };

    checkAuth();
  }, [performLogout]);

  const handleEditUser = (user) => {
    setEditingUser(user); 
    setActive("cadastro"); 
  };

  const handleOpenNewUser = () => {
    setEditingUser(null); 
    handleGoToAdd(); 
  };

  const handleViewReceipt = (venda) => {
    setSelectedSale(venda); 
    console.log("Venda selecionada para visualização:", venda);
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
        <h2>Carregando...</h2>
      </div>
    );
  }

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
        
        {/* --- AQUI ESTÁ A DIV CONTAINER (VOLTOU PARA CÁ) --- */}
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
        {/* --- FIM DA DIV CONTAINER --- */}

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
          isOpen={!!selectedSale} 
          onClose={() => setSelectedSale(null)} 
          variant="receipt"
          data={selectedSale} 
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