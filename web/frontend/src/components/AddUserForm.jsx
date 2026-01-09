import React from "react";
import "../styles/AddUserForm.css";
import iconHeader from "../assets/icon-add-blue.svg"; 

export function AddUserForm() {
  
  return (
    <div className="add-user-container">
      
      <div className="add-user-header">
        <img src={iconHeader} alt="Icone" className="header-icon" />
        <h2 className="add-user-title">CADASTRAR NOVO USUÁRIO</h2>
      </div>

      <form className="add-user-form">
        <div className="form-group">
          <label className="form-label">Usuário</label>
          <input type="text" className="form-input" required />
        </div>

        <div className="form-group">
          <label className="form-label">Nome</label>
          <input type="text" className="form-input" required />
        </div>

        <div className="form-group">
          <label className="form-label">Empresa</label>
          <input type="text" className="form-input" required />
        </div>

        <div className="form-group">
          <label className="form-label">CNPJ</label>
          <input type="text" className="form-input" required />
        </div>
      </form>
    </div>
  );
}