import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import Login from './pages/Login/Login';
import Dashboard from './pages/Dashboard/Dashboard';
import Users from './pages/Users/Users';
import Products from './pages/Products/Products';
import Imports from './pages/Imports/Imports';
import Exports from './pages/Exports/Exports';
import TrustLogs from './pages/TrustLogs/TrustLogs';
import { ProtectedRoute } from './components/ProtectedRoute';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: 'login',
        element: <Login />,
      },
      {
        path: '/',
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: '/dashboard',
        element: (
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: '/users',
        element: (
          <ProtectedRoute requiredRole="ADMIN">
            <Users />
          </ProtectedRoute>
        ),
      },
      {
        path: '/products',
        element: (
          <ProtectedRoute>
            <Products />
          </ProtectedRoute>
        ),
      },
      {
        path: '/imports',
        element: (
          <ProtectedRoute>
            <Imports />
          </ProtectedRoute>
        ),
      },
      {
        path: '/exports',
        element: (
          <ProtectedRoute>
            <Exports />
          </ProtectedRoute>
        ),
      },
      {
        path: '/trust-logs',
        element: (
          <ProtectedRoute requiredRole="ADMIN">
            <TrustLogs />
          </ProtectedRoute>
        ),
      },
    ],
  },
]);

export default router;