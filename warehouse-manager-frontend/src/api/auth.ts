import axios from 'axios';
import { API_BASE_URL } from '../utils/apiConfig';

export interface LoginCredentials {
  username: string;
  password: string;
}

export interface User {
  id: number;
  username: string;
  role: string;
  permissions: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export const login = async (credentials: LoginCredentials): Promise<AuthResponse> => {
  const response = await axios.post<AuthResponse>(`${API_BASE_URL}/api/auth/login`, credentials);
  return response.data;
};

export const refresh = async (refreshToken: string): Promise<AuthResponse> => {
  const response = await axios.post<AuthResponse>(`${API_BASE_URL}/api/auth/refresh`, {
    refreshToken
  });
  return response.data;
};

export interface TeleportUserResponse {
  authenticated: boolean;
  user?: {
    id: number;
    username: string;
    role: string;
    permissions: string[];
  };
  message?: string;
}

export const getTeleportIdentity = async (): Promise<TeleportUserResponse> => {
  try {
    console.log('[Teleport] Checking identity, API_BASE_URL:', API_BASE_URL);
    // Use withCredentials to ensure cookies are sent if available
    const response = await axios.get<TeleportUserResponse>(`${API_BASE_URL}/api/auth/teleport`, {
      withCredentials: true
    });
    console.log('[Teleport] Response:', response.data);
    return response.data;
  } catch (error: any) {
    // Log error for debugging
    console.error('[Teleport] Error checking identity:', error);
    console.error('[Teleport] Error details:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status,
      url: error.config?.url
    });
    // If error, return not authenticated
    // Check for Network Error (likely CORS on redirect due to missing backend session)
    if (error.code === 'ERR_NETWORK' && !error.response) {
      return {
        authenticated: false,
        message: 'BACKEND_AUTH_REQUIRED'
      };
    }

    return {
      authenticated: false,
      message: error.response?.data?.message || error.message || 'Failed to check Teleport identity'
    };
  }
};