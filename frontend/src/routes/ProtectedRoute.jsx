import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children }) {
  const isAuthenticated = !!localStorage.getItem("token"); // or your auth logic

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
