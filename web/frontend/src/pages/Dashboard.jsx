import { useState } from "react";
import "../styles/dashboard.css";

import logo from "../assets/logo.svg";
import usersIcon from "../assets/icon-users.svg";
import salesIcon from "../assets/icon-sales.svg";
import logoutIcon from "../assets/icon-logout.svg";
import editIcon from "../assets/icon-edit.svg";
import background from "../assets/background.png";

export default function Dashboard() {
  const [active, setActive] = useState("usuarios");

  const users = [
    { id: 1, usuario: "João", empresa: "Empresa X", cnpj: "12.345.678/0001-90" },
    { id: 2, usuario: "Maria", empresa: "Empresa Y", cnpj: "98.765.432/0001-10" },
  ];

  return (
    <div
      className="dashboard"
      style={{ backgroundImage: `url(${background})` }}
    >
      <aside className="sidebar">
        <img src={logo} className="sidebar-logo" />

        <div
          className={`menu-item ${active === "usuarios" ? "active" : ""}`}
          onClick={() => setActive("usuarios")}
        >
          <img src={usersIcon} />
          <span>Usuários</span>
        </div>

        <div
          className={`menu-item ${active === "vendas" ? "active" : ""}`}
          onClick={() => setActive("vendas")}
        >
          <img src={salesIcon} />
          <span>Vendas</span>
        </div>

        <div className="menu-item">
          <img src={logoutIcon} />
          <span>Logout</span>
        </div>
      </aside>

      <main className="content">
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th></th>
                <th>Usuário</th>
                <th>Empresa</th>
                <th>CNPJ</th>
                <th></th>
              </tr>
            </thead>

            <tbody>
              {users.map((u) => (
                <tr key={u.id}>
                  <td>
                    <div className="status-box"></div>
                  </td>
                  <td className="td-user">{u.usuario}</td>
                  <td className="td-data">{u.empresa}</td>
                  <td className="td-data">{u.cnpj}</td>
                  <td>
                    <img src={editIcon} className="edit-icon" />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}
