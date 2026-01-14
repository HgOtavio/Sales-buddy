import "../styles/ConfirmationModal.css";

export function ConfirmationModal({ 
  isOpen, 
  onClose, 
  onConfirm, 
  userName, 
  variant = "delete", 
  receiptImageSrc 
}) {
  
  if (!isOpen) return null;

  // --- MODO: VISUALIZAR COMPROVANTE ---
  if (variant === "receipt") {
    return (
      <div className="modal-overlay">
        {/* Adicionei 'receipt-mode' para estilizar diferente */}
        <div className="modal-box receipt-mode">
          
          {/* Removi o título H3 daqui */}
          
          <div className="receipt-content">
            {receiptImageSrc ? (
              <img src={receiptImageSrc} alt="Comprovante" />
            ) : (
              <div style={{ padding: '20px' }}>Imagem indisponível</div>
            )}
          </div>
          
          <div className="modal-actions">
            <button className="btn-modal btn-blue" onClick={() => alert("Imagem Salva!")}>
              Salvar
            </button>
            <button className="btn-modal btn-green" onClick={() => window.print()}>
              Imprimir
            </button>
            <button className="btn-modal btn-red" onClick={onClose}>
              Fechar
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay">
      <div className="modal-box">
        <h3 style={{ color: '#A32C2C', textAlign: 'left' }}>
            Você está prestes a excluir os seguintes usuários:
        </h3>
        
        <p>
          <strong>{userName}</strong>
        </p>
        
        <h3 style={{ color: '#A32C2C', textAlign: 'left' }}>
            Deseja prosseguir?
        </h3>
        
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