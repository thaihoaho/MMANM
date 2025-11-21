import api from '../utils/axios';
import { getPolicyContext } from '../utils/policyContext';

const API_BASE_URL = 'http://localhost:8081';

export interface Import {
  id: number;
  productId: number;
  quantity: number;
  createdAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  metadata: any;
}

export const getImports = async (): Promise<Import[]> => {
  const response = await api.get<ApiResponse<Import[]>>(`${API_BASE_URL}/api/imports`);
  return response.data.data;
};

export const getImportById = async (id: number): Promise<Import> => {
  const response = await api.get<ApiResponse<Import>>(`${API_BASE_URL}/api/imports/${id}`);
  return response.data.data;
};

export const createImport = async (importData: Omit<Import, 'id' | 'createdAt'>): Promise<Import> => {
  const context = getPolicyContext();
  const response = await api.post<ApiResponse<Import>>(`${API_BASE_URL}/api/imports`, {
    ...importData,
    context
  });
  return response.data.data;
};

export const updateImport = async (id: number, importData: Partial<Import>): Promise<Import> => {
  const context = getPolicyContext();
  const response = await api.put<ApiResponse<Import>>(`${API_BASE_URL}/api/imports/${id}`, {
    ...importData,
    context
  });
  return response.data.data;
};

export const deleteImport = async (id: number): Promise<void> => {
  const context = getPolicyContext();
  await api.delete<ApiResponse<void>>(`${API_BASE_URL}/api/imports/${id}`, {
    data: { context }
  });
};