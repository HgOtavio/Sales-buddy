import "../styles/FloatingButtons.css";
import iconPlus from "../assets/icon-add.svg"; 
import iconTrash from "../assets/icon-delete.svg";
import iconCheck from "../assets/icon-check.svg";
import iconKey from "../assets/icon-key.svg"; 

export function FloatingButtons({ activeTab, onDelete, onAdd, onReset, disabled, isEditing, formId }) {
  
  if (activeTab === "cadastro") {
    return (
      <div className="floating-buttons">
        
        <div className="action-group" onClick={onReset}>
          <div className="icon-outside icon-reset">
            <img src={iconKey} alt="Resetar" />
          </div>
          <button type="button" className="btn-text btn-reset-text">
            Resetar Senha
          </button>
        </div>

        <div className="action-group">
          <div className="icon-outside icon-save">
            <img src={iconCheck} alt="Salvar" />
          </div>
          <button 
            type="submit" 
            form={formId} 
            className="btn-text btn-save-text"
          >
            {isEditing ? "Salvar alterações" : "Salvar novo usuário"}
          </button>
        </div>

      </div>
    );
  }

  if (activeTab === "usuarios") {
    return (
      <div className="floating-buttons">
        
        <div 
          className={`action-group ${disabled ? "disabled-group" : ""}`} 
          onClick={!disabled ? onDelete : undefined}
        >
          <div className="icon-outside icon-delete">
            <img src={iconTrash} alt="Excluir" />
          </div>
          <button className="btn-text btn-delete-text" disabled={disabled}>
            Excluir usuário
          </button>
        </div>

        <div className="action-group" onClick={onAdd}>
          <div className="icon-outside icon-add">
            <img src={iconPlus} alt="Adicionar" />
          </div>
          <button className="btn-text btn-add-text">
            Cadastrar novo usuário
          </button>
        </div>

      </div>
    );
  }

  return null;
}