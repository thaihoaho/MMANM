import api from '../utils/axios';
import { getPolicyContext } from '../utils/policyContext';

const API_BASE_URL = 'http://localhost:8081';

export interface User {
  id: number;
  username: string;
  password?: string;
  role: string;
  permissions: string[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  metadata: any;
}

export const getUsers = async (): Promise<User[]> => {
  const response = await api.get<ApiResponse<User[]>>(`${API_BASE_URL}/api/users`);
  return response.data.data;
};

export const getUserById = async (id: number): Promise<User> => {
  const response = await api.get<ApiResponse<User>>(`${API_BASE_URL}/api/users/${id}`);
  return response.data.data;
};

export const createUser = async (userData: Omit<User, 'id'>): Promise<User> => {
  const context = getPolicyContext();
  const response = await api.post<ApiResponse<User>>(`${API_BASE_URL}/api/users`, {
    ...userData,
    context
  });
  return response.data.data;
};

export const updateUser = async (id: number, userData: Partial<User>): Promise<User> => {
  const context = getPolicyContext();
  const response = await api.put<ApiResponse<User>>(`${API_BASE_URL}/api/users/${id}`, {
    ...userData,
    context
  });
  return response.data.data;
};

export const deleteUser = async (id: number): Promise<void> => {
  const context = getPolicyContext();
  await api.delete<ApiResponse<void>>(`${API_BASE_URL}/api/users/${id}`, {
    data: { context }
  });
};