import React from 'react';

export function DeleteContent({ userName, onConfirm, onClose }) {
  return (
    <div className="modal-box" style={{ padding: '20px', maxWidth: '400px' }}>
      
      <h4 style={{ 
          color: '#A32C2C', 
          textAlign: 'left', 
          fontWeight: 'normal', 
          margin: '0 0 10px 0' 
      }}>
        Você está prestes a excluir os seguintes usuários:
      </h4>

      <div style={{ 
          margin: '10px 0', 
          fontSize: '1.1rem', 
          whiteSpace: 'pre-line', 
          color: '#A32C2C',
          fontWeight: 'bold', 
          textAlign: 'left'
      }}>
          {userName || "Usuário(s) selecionado(s)"}
      </div>

      <h4 style={{ 
          color: '#A32C2C', 
          textAlign: 'left', 
          fontWeight: 'normal',
          margin: '10px 0 20px 0'
      }}>
          Você deseja prosseguir?
      </h4>

      <div className="modal-actions">
        <button className="btn-modal btn-green" onClick={onConfirm}>Sim</button>
        <button className="btn-modal btn-red" onClick={onClose}>Não</button>
      </div>
    </div>
  );
}