import editIcon from "../assets/icon-edit.svg";
import "../styles/UsersTable.css";

const maskCNPJ = (value) => {
  if (!value) return "";
  const pureValue = value.replace(/\D/g, "");
  return pureValue
    .replace(/^(\d{2})(\d)/, "$1.$2")
    .replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3")
    .replace(/\.(\d{3})(\d)/, ".$1/$2")
    .replace(/(\d{4})(\d)/, "$1-$2")
    .slice(0, 18);
};

export function UsersTable({ users, selectedIds, onSelect, onEdit }) {
  const userData = JSON.parse(localStorage.getItem('userData'));
  const meuId = userData?.id;

  return (
    <table>
      <thead>
        <tr>
          <th></th>
          <th>USUÁRIO</th>
          <th>NOME</th>
          <th>EMPRESA</th>
          <th>CNPJ</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {users.map((u) => {
          const isSelected = selectedIds.includes(u.id);
          const isMe = u.id === meuId;

          return (
            <tr key={u.id} style={isMe ? { opacity: 0.8 } : {}}>
              <td>
                <div
                  className={`status-box ${isSelected ? "box-active" : ""} ${isMe ? "box-disabled" : ""}`}
                  onClick={() => !isMe && onSelect(u.id)}
                  style={isMe ? { cursor: "not-allowed", backgroundColor: "#ccc" } : { cursor: "pointer" }}
                ></div>
              </td>
              <td className="td-user">
                {u.user} {isMe && <span style={{ fontSize: '10px', color: '#666' }}>(VOCÊ)</span>}
              </td>    
              <td className="td-data">{u.name}</td>     
              <td className="td-data">{u.company}</td>  
              <td className="td-data">{maskCNPJ(u.taxId)}</td>    
              <td>
                <img 
                  src={editIcon} 
                  className="edit-icon" 
                  alt="Edit" 
                  onClick={() => onEdit(u)} 
                  style={{ cursor: "pointer" }} 
                />
              </td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}