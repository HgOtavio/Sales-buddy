import React, { useState, useEffect } from "react";
import "../styles/AddUserForm.css";
import iconHeader from "../assets/icon-add-blue.svg"; 

// Recebe userToEdit (pode ser null) e onCancel/onSubmit para controle
export function AddUserForm({ userToEdit, onSubmit, onCancel }) {
  
  // Estado inicial do formulário
  const [formData, setFormData] = useState({
    usuario: "",
    nome: "",
    empresa: "",
    cnpj: ""
  });

  // Efeito: Sempre que 'userToEdit' mudar, atualiza o formulário
  useEffect(() => {
    if (userToEdit) {
      setFormData({
        usuario: userToEdit.usuario,
        nome: userToEdit.nome,
        empresa: userToEdit.empresa,
        cnpj: userToEdit.cnpj
      });
    } else {
      // Limpa se for cadastro novo
      setFormData({ usuario: "", nome: "", empresa: "", cnpj: "" });
    }
  }, [userToEdit]);

  // Função para lidar com a digitação
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Envia os dados atualizados junto com o ID se for edição
    onSubmit({ ...formData, id: userToEdit?.id }); 
  };

  return (
    <div className="add-user-container">
      
      <div className="add-user-header">
        <img src={iconHeader} alt="Icone" className="header-icon" />
        {/* Título Dinâmico */}
        <h2 className="add-user-title">
            {userToEdit ? "EDITAR USUÁRIO" : "CADASTRAR NOVO USUÁRIO"}
        </h2>
      </div>

      <form className="add-user-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Usuário</label>
          <input 
            type="text" 
            name="usuario" // Importante para o handleChange
            className="form-input" 
            value={formData.usuario} 
            onChange={handleChange}
            required 
          />
        </div>

        <div className="form-group">
          <label className="form-label">Nome</label>
          <input 
            type="text" 
            name="nome"
            className="form-input" 
            value={formData.nome} 
            onChange={handleChange}
            required 
          />
        </div>

        <div className="form-group">
          <label className="form-label">Empresa</label>
          <input 
            type="text" 
            name="empresa"
            className="form-input" 
            value={formData.empresa} 
            onChange={handleChange}
            required 
          />
        </div>

        <div className="form-group">
          <label className="form-label">CNPJ</label>
          <input 
            type="text" 
            name="cnpj"
            className="form-input" 
            value={formData.cnpj} 
            onChange={handleChange}
            required 
          />
        </div>

        
      </form>
    </div>
  );
}