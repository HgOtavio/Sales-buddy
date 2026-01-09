import { useState } from "react";
import "../styles/dashboard.css";

// Hooks
import { useUsers } from "../hooks/useUsers";
import { useFloatingActions } from "../hooks/useFloatingActions"; // Importe o novo hook

// Componentes
import { UsersTable } from "../components/UsersTable";
import { SalesTable } from "../components/SalesTable"; 
import { AddUserForm } from "../components/AddUserForm"; 
import { SidebarItem } from "../components/SidebarItem";
import { FloatingButtons } from "../components/FloatingButtons";
import { ConfirmationModal } from "../components/ConfirmationModal";

// Assets
import logo from "../assets/logo.svg";
import background from "../assets/background.png";
import usersIcon from "../assets/icon-users.svg";
import salesIcon from "../assets/icon-sales.svg";
import logoutIcon from "../assets/icon-logout.svg";
import usersIconBlue from "../assets/icon-users-blue.svg";
import salesIconBlue from "../assets/icon-sales-blue.svg";

export default function Dashboard() {
  const [active, setActive] = useState("usuarios");
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
          onClick={() => alert("Saindo...")} 
        />
      </aside>

      <main className="content">
        <div className="table-container">
          
          {active === "usuarios" && (
            <UsersTable 
              users={users} 
              selectedIds={selectedIds} 
              onSelect={toggleSelection} 
            />
          )}

          {active === "vendas" && <SalesTable />}

          {active === "cadastro" && <AddUserForm />}

        </div>
        <FloatingButtons 
          activeTab={active} 
          
          onDelete={requestDelete}
          disabled={selectedIds.length === 0}
          
          onAdd={handleGoToAdd}
          onSave={handleSave}
          onReset={handleResetPassword} 
        />

        <ConfirmationModal 
          isOpen={isModalOpen}
          onClose={cancelDelete}
          onConfirm={confirmDelete}
          userName={selectedNames} 
        />
        
      </main>
    </div>
  );
}