import "../styles/ConfirmationModal.css";

export function ConfirmationModal({ isOpen, onClose, onConfirm, userName }) {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h3>Você está prestes a excluir os seguintes usuários:</h3>
        
        <p>
          <strong>{userName}</strong>
        </p>
        
        <h3>Deseja prosseguir?</h3>
        
        <div className="modal-actions">
          <button className="btn-modal btn-green" onClick={onConfirm}>
            Sim
          </button>
          <button className="btn-modal btn-red" onClick={onClose}>
            Não
          </button>
        </div>
      </div>
    </div>
  );
}