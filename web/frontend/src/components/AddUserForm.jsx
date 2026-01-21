import React, { useState, useEffect, useRef } from "react";
import PropTypes from "prop-types"; 
// import { toast } from 'react-toastify'; // Removido pois não será usado aqui
import { maskCNPJ } from "../utils/masks"; 
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

  const nameRef = useRef(null);
  const userRef = useRef(null);
  const emailRef = useRef(null);
  const companyRef = useRef(null);
  const taxIdRef = useRef(null);

  const isReadyToSubmit = useRef(false);

  useEffect(() => {
    const timer = setTimeout(() => {
        isReadyToSubmit.current = true;
    }, 500);

    return () => clearTimeout(timer);
  }, []);
  

useEffect(() => {
    if (userToEdit && Object.keys(userToEdit).length > 0) {
      setFormData({
        user: userToEdit.user || "",
        name: userToEdit.name || "",
        company: userToEdit.company || "",
        taxId: userToEdit.taxId || "",
        email: userToEdit.email || ""
      });
    } else {
      setFormData({ user: "", name: "", company: "", taxId: "", email: "" });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(userToEdit), setFormData]);

  useEffect(() => {
    if (nameRef.current) {
      nameRef.current.focus();
    }
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    const finalValue = name === "taxId" ? maskCNPJ(value) : value;
    setFormData((prev) => ({ ...prev, [name]: finalValue }));
  };

  const handleKeyDown = (e, nextRef) => {
    if (e.key === "Enter") {
      if (nextRef && nextRef.current) {
        e.preventDefault();
        nextRef.current.focus();
      } 
    }
  };

  const handleSubmit = (e) => {
    if (e) e.preventDefault();

   
    if (!isReadyToSubmit.current) {
        return; 
    }

  
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

      <form id={formId} className="add-user-form" onSubmit={handleSubmit} noValidate>
        
        <div className="form-group">
          <label className="form-label">Nome</label>
          <input 
            type="text" 
            name="name" 
            ref={nameRef}
            className="form-input" 
            value={formData.name} 
            onChange={handleChange}
            onKeyDown={(e) => handleKeyDown(e, userRef)}
            autoComplete="off"
          />
        </div>

        <div className="form-group">
          <label className="form-label">Usuário</label>
          <input 
            type="text" 
            name="user" 
            ref={userRef}
            className="form-input" 
            value={formData.user} 
            onChange={handleChange}
            onKeyDown={(e) => handleKeyDown(e, emailRef)}
            autoComplete="off"
          />
        </div>

        <div className="form-group">
          <label className="form-label">E-mail</label>
          <input 
            type="email" 
            name="email"
            ref={emailRef}
            className="form-input" 
            value={formData.email} 
            onChange={handleChange}
            onKeyDown={(e) => handleKeyDown(e, companyRef)}
            autoComplete="off"
          />
        </div>

        <div style={{ display: 'flex', gap: '15px' }}>
            <div className="form-group" style={{ flex: 1 }}>
                <label className="form-label">Empresa</label>
                <input 
                    type="text" 
                    name="company"
                    ref={companyRef}
                    className="form-input" 
                    value={formData.company} 
                    onChange={handleChange}
                    onKeyDown={(e) => handleKeyDown(e, taxIdRef)}
                    autoComplete="off"
                />
            </div>

            <div className="form-group" style={{ flex: 1 }}>
                <label className="form-label">CNPJ</label>
                <input 
                    type="text" 
                    name="taxId"
                    ref={taxIdRef}
                    className="form-input" 
                    value={formData.taxId} 
                    onChange={handleChange}
                    onKeyDown={(e) => handleKeyDown(e, null)} 
                    placeholder="00.000.000/0000-00"
                    autoComplete="off"
                    maxLength={18}
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