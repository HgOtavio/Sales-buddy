import React, { useState, useEffect } from "react";
import PropTypes from "prop-types"; 
import "../styles/AddUserForm.css";
import iconHeader from "../assets/icon-add-blue.svg"; 

export function AddUserForm({ userToEdit, onSubmit, formId }) {
 
  const [formData, setFormData] = useState({
    user: "",    
    name: "",    
    company: "", 
    taxId: "",   
    email: ""
  });

  useEffect(() => {
    if (userToEdit) {
      setFormData({
        user: userToEdit.user || "",
        name: userToEdit.name || "",
        company: userToEdit.company || "",
        taxId: userToEdit.taxId || "",
        email: userToEdit.email || ""
      });
    } else {
      setFormData({ 
        user: "", 
        name: "", company: "", 
        taxId: "", email: "" });
    }
  }, [userToEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ ...formData, id: userToEdit?.id }); 
  };

  return (
    <div className="add-user-container">
      
      <div className="add-user-header">
        <img src={iconHeader} alt="Icone" className="header-icon" />
        <h2 className="add-user-title">
            {userToEdit ? "EDITAR DADOS" : "NOVO CADASTRO"}
        </h2>
      </div>

      <form id={formId} className="add-user-form" onSubmit={handleSubmit}>
        
        <div className="form-group">
          <label className="form-label">Nome Completo</label>
          <input 
            type="text" 
            name="name" 
            className="form-input" 
            value={formData.name} 
            onChange={handleChange}
            required 
          />
        </div>

        <div className="form-group">
          <label className="form-label">Usuário (Nick)</label>
          <input 
            type="text" 
            name="user" 
            className="form-input" 
            value={formData.user} 
            onChange={handleChange}
            required 
          />
        </div>

        <div className="form-group">
          <label className="form-label">E-mail</label>
          <input 
            type="email" 
            name="email"
            className="form-input" 
            value={formData.email} 
            onChange={handleChange}
            required 
          />
        </div>

        <div style={{ display: 'flex', gap: '15px' }}>
            <div className="form-group" style={{ flex: 1 }}>
                <label className="form-label">Empresa</label>
                <input 
                    type="text" 
                    name="company"
                    className="form-input" 
                    value={formData.company} 
                    onChange={handleChange}
                    required 
                />
            </div>

            <div className="form-group" style={{ flex: 1 }}>
                <label className="form-label">CNPJ</label>
                <input 
                    type="text" 
                    name="taxId"
                    className="form-input" 
                    value={formData.taxId} 
                    onChange={handleChange}
                    required 
                />
            </div>
        </div>
        
      </form>
    </div>
  );
}

AddUserForm.propTypes = {
  userToEdit: PropTypes.object,
  onSubmit: PropTypes.func.isRequired,
  formId: PropTypes.string
};