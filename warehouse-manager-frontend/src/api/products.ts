import api from '../utils/axios';
import { getPolicyContext } from '../utils/policyContext';

const API_BASE_URL = 'http://localhost:8081';

export interface Product {
  id: number;
  name: string;
  quantity: number;
  location: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  metadata: any;
}

export const getProducts = async (): Promise<Product[]> => {
  const response = await api.get<ApiResponse<Product[]>>(`${API_BASE_URL}/api/products`);
  return response.data.data;
};

export const getProductById = async (id: number): Promise<Product> => {
  const response = await api.get<ApiResponse<Product>>(`${API_BASE_URL}/api/products/${id}`);
  return response.data.data;
};

export const createProduct = async (productData: Omit<Product, 'id'>): Promise<Product> => {
  const context = getPolicyContext();
  const response = await api.post<ApiResponse<Product>>(`${API_BASE_URL}/api/products`, {
    ...productData,
    context
  });
  return response.data.data;
};

export const updateProduct = async (id: number, productData: Partial<Product>): Promise<Product> => {
  const context = getPolicyContext();
  const response = await api.put<ApiResponse<Product>>(`${API_BASE_URL}/api/products/${id}`, {
    ...productData,
    context
  });
  return response.data.data;
};

export const deleteProduct = async (id: number): Promise<void> => {
  const context = getPolicyContext();
  await api.delete<ApiResponse<void>>(`${API_BASE_URL}/api/products/${id}`, {
    data: { context }
  });
};