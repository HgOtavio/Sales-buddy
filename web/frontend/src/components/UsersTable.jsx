import editIcon from "../assets/icon-edit.svg";
import "../styles/UsersTable.css";

export function UsersTable({ users, selectedIds, onSelect, onEdit }) {
  return (
    <table>
      <thead>
        <tr>
          <th></th>
          <th>Usuário</th>
          <th>Nome</th>
          <th>Empresa</th>
          <th>CNPJ</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        {users.map((u) => {
          const isSelected = selectedIds.includes(u.id);
          return (
            <tr key={u.id}>
              <td>
                <div
                  className={`status-box ${isSelected ? "box-active" : ""}`}
                  onClick={() => onSelect(u.id)}
                ></div>
              </td>
              <td className="td-user">{u.usuario}</td>
              <td className="td-data">{u.nome}</td>
              <td className="td-data">{u.empresa}</td>
              <td className="td-data">{u.cnpj}</td>
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