import {
  Box,
  Container,
  Heading,
  SimpleGrid,
  Text,
  VStack,
} from '@chakra-ui/react';
import React, { useEffect, useState } from 'react';
import { useAuth } from '../../hooks/useAuth';
import { getUsers } from '../../api/users';
import { getProducts } from '../../api/products';
import { getImports } from '../../api/imports';
import { getExports } from '../../api/exports';
import { useToast } from '@chakra-ui/react';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalProducts: 0,
    totalTransactions: 0,
  });
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const [users, products, imports, exports] = await Promise.all([
        getUsers(),
        getProducts(),
        getImports(),
        getExports()
      ]);

      setStats({
        totalUsers: Array.isArray(users) ? users.length : 0,
        totalProducts: Array.isArray(products) ? products.length : 0,
        totalTransactions: (Array.isArray(imports) ? imports.length : 0) + (Array.isArray(exports) ? exports.length : 0),
      });
    } catch (error: any) {
      toast({
        title: 'Error loading dashboard stats',
        description: error.response?.data?.message || 'Failed to load dashboard statistics',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Container maxW="container.xl" py={8}>
        <Text>Loading dashboard...</Text>
      </Container>
    );
  }

  return (
    <Container maxW="container.xl" py={8}>
      <VStack spacing={8} align="stretch">
        <Heading size="lg">Dashboard</Heading>

        <Text>Welcome back, <strong>{user?.username}</strong>!</Text>

        <SimpleGrid columns={{ base: 1, md: 3 }} spacing={6}>
          <Box p={6} bg="white" rounded="md" boxShadow="md" borderLeft="4px" borderLeftColor="blue.500">
            <Text fontSize="lg" fontWeight="bold" color="gray.700">Total Users</Text>
            <Text fontSize="2xl" fontWeight="bold" color="blue.600">{stats.totalUsers}</Text>
            <Text fontSize="sm" color="gray.500">Manage user accounts</Text>
          </Box>

          <Box p={6} bg="white" rounded="md" boxShadow="md" borderLeft="4px" borderLeftColor="green.500">
            <Text fontSize="lg" fontWeight="bold" color="gray.700">Products</Text>
            <Text fontSize="2xl" fontWeight="bold" color="green.600">{stats.totalProducts}</Text>
            <Text fontSize="sm" color="gray.500">Track inventory items</Text>
          </Box>

          <Box p={6} bg="white" rounded="md" boxShadow="md" borderLeft="4px" borderLeftColor="orange.500">
            <Text fontSize="lg" fontWeight="bold" color="gray.700">Transactions</Text>
            <Text fontSize="2xl" fontWeight="bold" color="orange.600">{stats.totalTransactions}</Text>
            <Text fontSize="sm" color="gray.500">Import/Export records</Text>
          </Box>
        </SimpleGrid>

        <Box p={6} bg="white" rounded="md" boxShadow="md">
          <Heading size="md" mb={4}>System Information</Heading>
          <Text>Role: <strong>{user?.role || 'N/A'}</strong></Text>
          <Text>Permissions: <strong>{user?.permissions ? user.permissions.join(', ') : 'N/A'}</strong></Text>
          <Text mt={2}>Zero Trust Architecture: <strong>Enabled</strong></Text>
        </Box>
      </VStack>
    </Container>
  );
};

export default Dashboard;