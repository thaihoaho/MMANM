import { Navigate, useLocation } from 'react-router-dom';
import useAuthStore from '../store/auth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;  // Optional role requirement
  requiredPermission?: string;  // Optional permission requirement
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  requiredRole,
  requiredPermission
}) => {
  const { user, isAuthenticated } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated) {
    // Redirect to login page with the current location as return url
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check role-based access if required
  if (requiredRole && user?.role !== requiredRole) {
    // Redirect to dashboard if user doesn't have required role
    return <Navigate to="/dashboard" replace />;
  }

  // Check permission-based access if required
  if (requiredPermission && user && !user.permissions.includes(requiredPermission)) {
    // Redirect to dashboard if user doesn't have required permission
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};