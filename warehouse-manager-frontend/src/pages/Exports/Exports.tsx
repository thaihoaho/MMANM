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
import { Export, getExports, createExport, updateExport, deleteExport } from '../../api/exports';

const Exports: React.FC = () => {
  const [exports, setExports] = useState<Export[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingExport, setEditingExport] = useState<Export | null>(null);
  const [formData, setFormData] = useState<Omit<Export, 'id' | 'createdAt'>>({ 
    productId: 0, 
    quantity: 0 
  });
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();

  useEffect(() => {
    fetchExports();
  }, []);

  const fetchExports = async () => {
    try {
      setLoading(true);
      const data = await getExports();
      // Ensure the data is an array
      if (Array.isArray(data)) {
        setExports(data);
      } else {
        setExports([]);
        toast({
          title: 'Error fetching exports',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    } catch (error: any) {
      setExports([]);
      toast({
        title: 'Error fetching exports',
        description: error.response?.data?.message || 'Failed to load exports',
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
      if (editingExport) {
        // Update existing export
        await updateExport(editingExport.id, formData);
        toast({
          title: 'Export updated successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      } else {
        // Create new export
        await createExport(formData);
        toast({
          title: 'Export created successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
      }
      onClose();
      fetchExports(); // Refresh the list
      resetForm();
    } catch (error) {
      toast({
        title: 'Error saving export',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    }
  };

  const handleDelete = async (exportId: number) => {
    if (window.confirm('Are you sure you want to delete this export?')) {
      try {
        await deleteExport(exportId);
        toast({
          title: 'Export deleted successfully',
          status: 'success',
          duration: 3000,
          isClosable: true,
        });
        fetchExports(); // Refresh the list
      } catch (error) {
        toast({
          title: 'Error deleting export',
          status: 'error',
          duration: 5000,
          isClosable: true,
        });
      }
    }
  };

  const handleEdit = (exp: Export) => {
    setEditingExport(exp);
    setFormData({
      productId: exp.productId,
      quantity: exp.quantity,
    });
    onOpen();
  };

  const handleAdd = () => {
    setEditingExport(null);
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
          <Heading size="lg">Exports</Heading>
          <Button colorScheme="blue" onClick={handleAdd}>
            Add Export
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
                {exports.map((exp) => (
                  <Tr key={exp.id}>
                    <Td>{exp.id}</Td>
                    <Td>{exp.productId}</Td>
                    <Td>{exp.quantity}</Td>
                    <Td>{exp.createdAt ? new Date(exp.createdAt).toLocaleString() : 'N/A'}</Td>
                    <Td>
                      <Button size="sm" mr={2} onClick={() => handleEdit(exp)}>
                        Edit
                      </Button>
                      <Button size="sm" colorScheme="red" onClick={() => handleDelete(exp.id)}>
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

      {/* Export Form Modal */}
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{editingExport ? 'Edit Export' : 'Add Export'}</ModalHeader>
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
              {editingExport ? 'Update' : 'Create'}
            </Button>
            <Button onClick={onClose}>Cancel</Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Container>
  );
};

export default Exports;