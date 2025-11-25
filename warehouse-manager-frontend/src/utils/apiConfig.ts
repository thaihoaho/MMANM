/**
 * Get API base URL based on current environment
 * - If running through Teleport (warehouse-frontend.localhost:3080) → use backend via Teleport
 * - Otherwise → use local backend
 */
export const getApiBaseUrl = (): string => {
  // Server-side / tests: call backend directly
  if (typeof window === 'undefined') {
    return 'http://localhost:8081';
  }

  const { hostname, port, protocol } = window.location;

  // When served via Teleport frontend app, forward requests to the Teleport backend app
  // Update: We now use relative paths (via window.location.origin) to route requests 
  // through the frontend server (Vite proxy), which forwards to the backend.
  // This avoids CORS issues and ensures auth headers are passed correctly.
  if (hostname === 'warehouse-frontend.localhost' || hostname.includes('warehouse-frontend')) {
    return window.location.origin;
  }

  // Local dev (Vite) just talks to its own origin (proxy handles /api -> 8081)
  return window.location.origin;
};

export const API_BASE_URL = getApiBaseUrl();

// Log API_BASE_URL for debugging (only in browser)
if (typeof window !== 'undefined') {
  console.log('[API Config] Detected environment:', {
    hostname: window.location.hostname,
    port: window.location.port,
    protocol: window.location.protocol,
    apiBaseUrl: API_BASE_URL
  });
}

