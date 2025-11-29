import {
  ChakraProvider,
  Box,
  theme,
  Flex
} from '@chakra-ui/react';
import { Outlet } from 'react-router-dom';
import Layout from './components/Layout';

function App() {
  return (
    <ChakraProvider theme={theme}>
      <Flex direction="column" minHeight="100vh">
        <Layout />
        <Box flex="1" w="100%" p={4} bg="#f7fafc">
          <Outlet />
        </Box>
      </Flex>
    </ChakraProvider>
  );
}

export default App;