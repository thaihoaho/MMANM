import {
  Box,
  Button,
  Container,
  Flex,
  FormControl,
  FormLabel,
  Heading,
  Input,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Table,
  TableContainer,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
  useDisclosure,
  useToast,
  VStack,
} from '@chakra-ui/react';
import React, { useEffect, useState } from 'react';
import { Import, getImports, createImport, updateImport, deleteImport } from '../../api/imports';

const Imports: React.FC = () => {
  const [imports, setImports] = useState<Import[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingImport, setEditingImport] = useState<Import | null>(null);
  const [formData, setFormData] = useState<Omit<Import, 'id' | 'createdAt'>>({ 
    productId: 0, 
    quantity: 0 
  });
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();

  useEffect(() => {
    fetchImports();
  }, []);

  const fetchImports = async () => {
    try {
      setLoading(true);
      const data = await getImports();
      // Ensure the data is an array
      if (Array.isArray(data)) {
        setImports(data);
      } else {
        setImports([]);
        toast({
          title: 'Error fetching imports',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error: any) {
      setImports([]);
      toast({
        title: 'Error fetching imports',
        description: error.response?.data?.message || 'Failed to load imports',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async () => {
    try {
      if (editingImport) {
        // Update existing import
        await updateImport(editingImport.id, formData);
        toast({
          title: 'Import updated successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      } else {
        // Create new import
        await createImport(formData);
        toast({
          title: 'Import created successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
      onClose();
      fetchImports(); // Refresh the list
      resetForm();
    } catch (error) {
      toast({
        title: 'Error saving import',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleDelete = async (importId: number) => {
    if (window.confirm('Are you sure you want to delete this import?')) {
      try {
        await deleteImport(importId);
        toast({
          title: 'Import deleted successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
        fetchImports(); // Refresh the list
      } catch (error) {
        toast({
          title: 'Error deleting import',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    }
  };

  const handleEdit = (imp: Import) => {
    setEditingImport(imp);
    setFormData({
      productId: imp.productId,
      quantity: imp.quantity,
    });
    onOpen();
  };

  const handleAdd = () => {
    setEditingImport(null);
    resetForm();
    onOpen();
  };

  const resetForm = () => {
    setFormData({
      productId: 0,
      quantity: 0,
    });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: name === 'quantity' || name === 'productId' ? Number(value) : value,
    });
  };

  return (
    <Container maxW="container.xl" py={8}>
      <VStack spacing={8} align="stretch">
        <Flex justifyContent="space-between" alignItems="center">
          <Heading size="lg">Imports</Heading>
          <Button colorScheme="blue" onClick={handleAdd}>
            Add Import
          </Button>
        </Flex>
        
        <Box bg="white" p={6} rounded="md" boxShadow="md">
          <TableContainer>
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th>ID</Th>
                  <Th>Product ID</Th>
                  <Th>Quantity</Th>
                  <Th>Created At</Th>
                  <Th>Actions</Th>
                </Tr>
              </Thead>
              <Tbody>
                {imports.map((imp) => (
                  <Tr key={imp.id}>
                    <Td>{imp.id}</Td>
                    <Td>{imp.productId}</Td>
                    <Td>{imp.quantity}</Td>
                    <Td>{imp.createdAt ? new Date(imp.createdAt).toLocaleString() : 'N/A'}</Td>
                    <Td>
                      <Button size="sm" mr={2} onClick={() => handleEdit(imp)}>
                        Edit
                      </Button>
                      <Button size="sm" colorScheme="red" onClick={() => handleDelete(imp.id)}>
                        Delete
                      </Button>
                    </Td>
                  </Tr>
                ))}
              </Tbody>
            </Table>
          </TableContainer>
        </Box>
      </VStack>

      {/* Import Form Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{editingImport ? 'Edit Import' : 'Add Import'}</ModalHeader>
          <ModalCloseButton />
          <ModalBody pb={6}>
            <VStack spacing={4} align="stretch">
              <FormControl isRequired>
                <FormLabel>Product ID</FormLabel>
                <Input
                  name="productId"
                  type="number"
                  value={formData.productId}
                  onChange={handleChange}
                  placeholder="Product ID"
                />
              </FormControl>

              <FormControl isRequired>
                <FormLabel>Quantity</FormLabel>
                <Input
                  name="quantity"
                  type="number"
                  value={formData.quantity}
                  onChange={handleChange}
                  placeholder="Quantity"
                />
              </FormControl>
            </VStack>
          </ModalBody>

          <ModalFooter>
            <Button colorScheme="blue" mr={3} onClick={handleSubmit}>
              {editingImport ? 'Update' : 'Create'}
            </Button>
            <Button onClick={onClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Container>
  );
};

export default Imports;