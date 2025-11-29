import api from '../utils/axios';
import { getPolicyContext } from '../utils/policyContext';

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
  const response = await api.get<ApiResponse<Export[]>>('/api/exports');
  return response.data.data;
};

export const getExportById = async (id: number): Promise<Export> => {
  const response = await api.get<ApiResponse<Export>>(`/api/exports/${id}`);
  return response.data.data;
};

export const createExport = async (exportData: Omit<Export, 'id' | 'createdAt'>): Promise<Export> => {
  const context = getPolicyContext();
  const response = await api.post<ApiResponse<Export>>('/api/exports', {
    ...exportData,
    context
  });
  return response.data.data;
};

export const updateExport = async (id: number, exportData: Partial<Export>): Promise<Export> => {
  const context = getPolicyContext();
  const response = await api.put<ApiResponse<Export>>(`/api/exports/${id}`, {
    ...exportData,
    context
  });
  return response.data.data;
};

export const deleteExport = async (id: number): Promise<void> => {
  const context = getPolicyContext();
  await api.delete<ApiResponse<void>>(`/api/exports/${id}`, {
    data: { context }
  });
};