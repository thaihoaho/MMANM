import { useEffect } from 'react';
import useAuthStore from '../store/auth';

export const useAuth = () => {
  const { user, isAuthenticated, accessToken, refreshToken, refreshAuth } = useAuthStore();

  useEffect(() => {
    // On component mount, check if we have a refresh token and the access token might be expired
    if (refreshToken && !accessToken && !isAuthenticated) {
      refreshAuth(refreshToken);
    }
  }, [refreshToken, accessToken, isAuthenticated, refreshAuth]);

  return { user, isAuthenticated, accessToken, refreshToken, refreshAuth };
};