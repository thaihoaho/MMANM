import { Navigate, useLocation } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { Spinner, Box, Text } from '@chakra-ui/react';
import useAuthStore from '../store/auth';
import { getTeleportIdentity } from '../api/auth';

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
  // Use a single selector to get all needed state
  const authState = useAuthStore((state) => ({
    isAuthenticated: state.isAuthenticated,
    user: state.user,
    updateAuth: state.updateAuth,
  }));
  
  const location = useLocation();
  const [checkingTeleport, setCheckingTeleport] = useState(!authState.isAuthenticated);

  // Check Teleport identity
  useEffect(() => {
    const isTeleportEnv = window.location.hostname.includes('warehouse-frontend.localhost') || 
                         window.location.hostname.includes('warehouse-frontend');

    // If not authenticated OR running in Teleport env (to ensure sync), check identity
    if (!authState.isAuthenticated || isTeleportEnv) {
      setCheckingTeleport(true);
      const checkTeleport = async () => {
        try {
          const response = await getTeleportIdentity();
          
          if (response.authenticated && response.user) {
            // Check if user changed or just logging in
            const userChanged = !authState.user || authState.user.username !== response.user.username;
            
            if (userChanged || !authState.isAuthenticated) {
              console.log('[ProtectedRoute] Updating auth state from Teleport identity:', response.user.username);
              // Auto-authenticate/Update with Teleport identity
              authState.updateAuth({
                accessToken: 'teleport-authenticated',
                refreshToken: 'teleport-authenticated',
                user: {
                  id: response.user.id,
                  username: response.user.username,
                  role: response.user.role,
                  permissions: response.user.permissions || [],
                },
              });
            }
          } else if (isTeleportEnv && authState.isAuthenticated) {
            // If Teleport says not authenticated but we have local state, verify if session invalid
            // In this case, maybe we should logout to be safe?
            // For now, let's rely on API calls failing with 401 to trigger logout
          }
        } catch (error) {
          console.error('Error checking Teleport identity:', error);
        } finally {
          setCheckingTeleport(false);
        }
      };

      checkTeleport();
    } else {
      setCheckingTeleport(false);
    }
  }, [authState.isAuthenticated, authState.updateAuth, authState.user]);

  // Show loading while checking Teleport
  if (checkingTeleport) {
    return (
      <Box textAlign="center" py={12}>
        <Spinner size="xl" />
        <Text mt={4}>Checking authentication...</Text>
      </Box>
    );
  }

  // Use reactive state from store
  if (!authState.isAuthenticated) {
    // Redirect to login page with the current location as return url
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check role-based access if required
  if (requiredRole && authState.user?.role !== requiredRole) {
    // Redirect to dashboard if user doesn't have required role
    return <Navigate to="/dashboard" replace />;
  }

  // Check permission-based access if required
  if (requiredPermission && authState.user && !authState.user.permissions.includes(requiredPermission)) {
    // Redirect to dashboard if user doesn't have required permission
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
};