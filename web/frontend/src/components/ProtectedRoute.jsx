import { Navigate } from "react-router-dom";

export function ProtectedRoute({ children }) {
  const token = localStorage.getItem("salesToken");
  const userData = localStorage.getItem("userData");

  if (!token || !userData) {
    return <Navigate to="/login" replace />;
  }

  return children;
}