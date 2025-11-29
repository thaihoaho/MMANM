import api from '../utils/axios';
import { getPolicyContext } from '../utils/policyContext';

const API_BASE_URL = 'http://localhost:8081';

export interface Export {
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

export const getExports = async (): Promise<Export[]> => {
  const response = await api.get<ApiResponse<Export[]>>(`${API_BASE_URL}/api/exports`);
  return response.data.data;
};

export const getExportById = async (id: number): Promise<Export> => {
  const response = await api.get<ApiResponse<Export>>(`${API_BASE_URL}/api/exports/${id}`);
  return response.data.data;
};

export const createExport = async (exportData: Omit<Export, 'id' | 'createdAt'>): Promise<Export> => {
  const context = getPolicyContext();
  const response = await api.post<ApiResponse<Export>>(`${API_BASE_URL}/api/exports`, {
    ...exportData,
    context
  });
  return response.data.data;
};

export const updateExport = async (id: number, exportData: Partial<Export>): Promise<Export> => {
  const context = getPolicyContext();
  const response = await api.put<ApiResponse<Export>>(`${API_BASE_URL}/api/exports/${id}`, {
    ...exportData,
    context
  });
  return response.data.data;
};

export const deleteExport = async (id: number): Promise<void> => {
  const context = getPolicyContext();
  await api.delete<ApiResponse<void>>(`${API_BASE_URL}/api/exports/${id}`, {
    data: { context }
  });
};