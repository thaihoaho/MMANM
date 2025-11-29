import api from '../utils/axios';

export interface TrustLog {
  id: number;
  userId: number | null;
  username: string;
  resource: string;
  action: string;
  ipAddress: string;
  trustScore: number;
  decisionResult: boolean;
  reason: string;
  timestamp: string; // ISO string format
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  metadata: any;
}

export interface TimeRangeParams {
  start: string; // ISO string
  end: string; // ISO string
}

export interface PageParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    paged: boolean;
    unpaged: boolean;
  };
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
}

// Fetch all trust logs (without pagination)
export const getAllTrustLogs = async (): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>('/api/trust-logs');
  return response.data.data;
};

// Fetch paginated trust logs
export const getPaginatedTrustLogs = async (params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>('/api/trust-logs', {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch paginated trust logs with multiple filters
export const getPaginatedTrustLogsWithFilters = async (
  resource?: string,
  action?: string,
  decisionResult?: boolean,
  userId?: number,
  username?: string,
  params: PageParams = { page: 0, size: 10, sortBy: 'id', sortDir: 'desc' }
): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>('/api/trust-logs', {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc',
      resource,
      action,
      decisionResult,
      userId,
      username
    }
  });
  return response.data.data;
};

// Fetch trust logs by user ID
export const getTrustLogsByUserId = async (userId: number): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/user/${userId}`);
  return response.data.data;
};

// Fetch paginated trust logs by user ID
export const getPaginatedTrustLogsByUserId = async (userId: number, params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/user/${userId}`, {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by username
export const getTrustLogsByUsername = async (username: string): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/username/${username}`);
  return response.data.data;
};

// Fetch paginated trust logs by username
export const getPaginatedTrustLogsByUsername = async (username: string, params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/username/${username}`, {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by resource
export const getTrustLogsByResource = async (resource: string): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/resource/${resource}`);
  return response.data.data;
};

// Fetch paginated trust logs by resource
export const getPaginatedTrustLogsByResource = async (resource: string, params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/resource/${resource}`, {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by action
export const getTrustLogsByAction = async (action: string): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/action/${action}`);
  return response.data.data;
};

// Fetch paginated trust logs by action
export const getPaginatedTrustLogsByAction = async (action: string, params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/action/${action}`, {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by decision result (true for granted, false for denied)
export const getTrustLogsByDecision = async (decisionResult: boolean): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/decision/${decisionResult}`);
  return response.data.data;
};

// Fetch paginated trust logs by decision result
export const getPaginatedTrustLogsByDecision = async (decisionResult: boolean, params: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/decision/${decisionResult}`, {
    params: {
      page: params.page,
      size: params.size,
      sortBy: params.sortBy || 'id',
      sortDir: params.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by time range
export const getTrustLogsByTimeRange = async (params: TimeRangeParams): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>('/api/trust-logs/time-range', {
    params: {
      start: params.start,
      end: params.end
    }
  });
  return response.data.data;
};

// Fetch paginated trust logs by time range
export const getPaginatedTrustLogsByTimeRange = async (timeRangeParams: TimeRangeParams, pageParams: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>('/api/trust-logs/time-range', {
    params: {
      start: timeRangeParams.start,
      end: timeRangeParams.end,
      page: pageParams.page,
      size: pageParams.size,
      sortBy: pageParams.sortBy || 'id',
      sortDir: pageParams.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by user ID and time range
export const getTrustLogsByUserAndTimeRange = async (userId: number, params: TimeRangeParams): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/user/${userId}/time-range`, {
    params: {
      start: params.start,
      end: params.end
    }
  });
  return response.data.data;
};

// Fetch paginated trust logs by user ID and time range
export const getPaginatedTrustLogsByUserAndTimeRange = async (userId: number, timeRangeParams: TimeRangeParams, pageParams: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/user/${userId}/time-range`, {
    params: {
      start: timeRangeParams.start,
      end: timeRangeParams.end,
      page: pageParams.page,
      size: pageParams.size,
      sortBy: pageParams.sortBy || 'id',
      sortDir: pageParams.sortDir || 'desc'
    }
  });
  return response.data.data;
};

// Fetch trust logs by username and time range
export const getTrustLogsByUsernameAndTimeRange = async (username: string, params: TimeRangeParams): Promise<TrustLog[]> => {
  const response = await api.get<ApiResponse<TrustLog[]>>(`/api/trust-logs/username/${username}/time-range`, {
    params: {
      start: params.start,
      end: params.end
    }
  });
  return response.data.data;
};

// Fetch paginated trust logs by username and time range
export const getPaginatedTrustLogsByUsernameAndTimeRange = async (username: string, timeRangeParams: TimeRangeParams, pageParams: PageParams): Promise<PageResponse<TrustLog>> => {
  const response = await api.get<ApiResponse<PageResponse<TrustLog>>>(`/api/trust-logs/username/${username}/time-range`, {
    params: {
      start: timeRangeParams.start,
      end: timeRangeParams.end,
      page: pageParams.page,
      size: pageParams.size,
      sortBy: pageParams.sortBy || 'id',
      sortDir: pageParams.sortDir || 'desc'
    }
  });
  return response.data.data;
};