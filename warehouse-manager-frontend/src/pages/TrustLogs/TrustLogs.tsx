import React, { useState, useEffect, useRef } from 'react';
import {
  Box,
  Card,
  CardHeader,
  CardBody,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  TableContainer,
  Text,
  Input,
  Select,
  Button,
  Flex,
  useColorModeValue,
  Skeleton,
  Badge,
  useToast,
  Icon,
  Tooltip,
  IconButton
} from '@chakra-ui/react';
import { FaRedo, FaFilter, FaSyncAlt, FaChevronLeft, FaChevronRight, FaEllipsisH } from 'react-icons/fa';
import { getAllTrustLogs, getPaginatedTrustLogs, getPaginatedTrustLogsWithFilters, TrustLog, TimeRangeParams, getTrustLogsByUserId, getTrustLogsByResource, getTrustLogsByAction, getTrustLogsByDecision, getTrustLogsByTimeRange, PageResponse, PageParams, getPaginatedTrustLogsByUserId, getPaginatedTrustLogsByResource, getPaginatedTrustLogsByAction, getPaginatedTrustLogsByDecision, getPaginatedTrustLogsByTimeRange } from '../../api/trustLogs';

const TrustLogsPage: React.FC = () => {
  const [trustLogs, setTrustLogs] = useState<TrustLog[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdate, setLastUpdate] = useState<Date>(new Date());
  const [autoRefresh, setAutoRefresh] = useState<boolean>(false);

  // Pagination state
  const [currentPage, setCurrentPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [pageSize, setPageSize] = useState<number>(10);
  const [sortBy, setSortBy] = useState<string>('id');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('desc');

  // Filter states
  const [userIdFilter, setUserIdFilter] = useState<string>('');
  const [resourceFilter, setResourceFilter] = useState<string>('');
  const [actionFilter, setActionFilter] = useState<string>('');
  const [decisionFilter, setDecisionFilter] = useState<string>('all');
  const [dateFromFilter, setDateFromFilter] = useState<string>('');
  const [dateToFilter, setDateToFilter] = useState<string>('');

  const toast = useToast();
  const cardBg = useColorModeValue('white', 'gray.800');
  const tableRowHover = useColorModeValue('gray.50', 'gray.700');
  const refreshInterval = useRef<NodeJS.Timeout | null>(null);

  // Fetch paginated trust logs with optional filters
  const fetchTrustLogs = async () => {
    try {
      setLoading(true);
      setError(null);

      // Create page params
      const pageParams: PageParams = {
        page: currentPage,
        size: pageSize,
        sortBy: sortBy,
        sortDir: sortDir
      };

      // Use the new combined filter endpoint to apply all active filters
      let pageResponse: PageResponse<TrustLog>;

      // Convert decision filter to boolean
      const decisionResult = decisionFilter !== 'all' ? decisionFilter === 'granted' : undefined;

      // Call the combined filter endpoint
      pageResponse = await getPaginatedTrustLogsWithFilters(
        resourceFilter || undefined,
        actionFilter || undefined,
        decisionResult,
        userIdFilter ? parseInt(userIdFilter) : undefined,
        undefined, // username filter (not currently in the UI)
        pageParams
      );

      setTrustLogs(pageResponse?.content || []);
      setTotalPages(pageResponse?.totalPages || 0);
      setTotalElements(pageResponse?.totalElements || 0);
      setLastUpdate(new Date());
    } catch (err) {
      setError('Failed to fetch trust logs');
      console.error('Error fetching trust logs:', err);
      toast({
        title: 'Error',
        description: 'Failed to load trust logs. Please try again later.',
        status: 'error',
        duration: 5000,
        isClosable: true,
      });
    } finally {
      setLoading(false);
    }
  };

  // Load logs on component mount
  useEffect(() => {
    fetchTrustLogs();

    // Clean up interval on component unmount
    return () => {
      if (refreshInterval.current) {
        clearInterval(refreshInterval.current);
      }
    };
  }, [currentPage, pageSize, sortBy, sortDir]); // Add pagination parameters to dependency array

  // Handle auto-refresh
  useEffect(() => {
    if (autoRefresh) {
      refreshInterval.current = setInterval(() => {
        fetchTrustLogs();
      }, 5000); // Refresh every 5 seconds
    } else {
      if (refreshInterval.current) {
        clearInterval(refreshInterval.current);
        refreshInterval.current = null;
      }
    }

    return () => {
      if (refreshInterval.current) {
        clearInterval(refreshInterval.current);
      }
    };
  }, [autoRefresh]);

  // Pagination handlers
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(parseInt(e.target.value));
    setCurrentPage(0); // Reset to first page when page size changes
  };

  // Refresh logs
  const handleRefresh = () => {
    fetchTrustLogs();
  };

  // Sorting handler
  const handleSort = (column: string) => {
    if (sortBy === column) {
      // If clicking the same column, toggle sort direction
      setSortDir(sortDir === 'asc' ? 'desc' : 'asc');
    } else {
      // If clicking a different column, sort by that column in descending order
      setSortBy(column);
      setSortDir('desc');
    }
  };

  // Reset filters
  const handleResetFilters = () => {
    setUserIdFilter('');
    setResourceFilter('');
    setActionFilter('');
    setDecisionFilter('all');
    setDateFromFilter('');
    setDateToFilter('');
    setCurrentPage(0); // Reset to first page when filters are reset
    fetchTrustLogs();
  };

  return (
    <Box p={6}>
      <Card bg={cardBg} boxShadow="md">
        <CardHeader>
          <Flex justify="space-between" align="center">
            <Box>
              <Text fontSize="xl" fontWeight="bold">Trust Decision Logs</Text>
              <Text fontSize="sm" color="gray.500">
                Last updated: {lastUpdate.toLocaleTimeString()} {autoRefresh && <Badge ml={2} colorScheme="blue">Auto-refreshing</Badge>}
              </Text>
            </Box>
            <Flex gap={3}>
              <Tooltip label={autoRefresh ? "Stop auto-refresh" : "Start auto-refresh"}>
                <Button
                  colorScheme={autoRefresh ? "red" : "blue"}
                  onClick={() => setAutoRefresh(!autoRefresh)}
                  leftIcon={<Icon as={FaSyncAlt} />}
                >
                  {autoRefresh ? 'Stop Auto-Refresh' : 'Auto-Refresh'}
                </Button>
              </Tooltip>

              <Tooltip label="Refresh now">
                <Button colorScheme="blue" onClick={handleRefresh} isLoading={loading} leftIcon={<Icon as={FaRedo} />}>
                  Refresh
                </Button>
              </Tooltip>

              <Button colorScheme="gray" onClick={handleResetFilters} leftIcon={<Icon as={FaFilter} />}>
                Reset Filters
              </Button>
            </Flex>
          </Flex>
        </CardHeader>
        <CardBody>
          {/* Filters Section */}
          <Box mb={6} p={4} bg={useColorModeValue('gray.50', 'gray.700')} borderRadius="md">
            <Flex gap={4} wrap="wrap" align="end">
              <Box>
                <Text fontSize="sm" mb={1}>User ID</Text>
                <Input
                  width="150px"
                  placeholder="Filter by user ID"
                  value={userIdFilter}
                  onChange={(e) => setUserIdFilter(e.target.value)}
                />
              </Box>

              <Box>
                <Text fontSize="sm" mb={1}>Resource</Text>
                <Input
                  width="150px"
                  placeholder="Filter by resource"
                  value={resourceFilter}
                  onChange={(e) => setResourceFilter(e.target.value)}
                />
              </Box>

              <Box>
                <Text fontSize="sm" mb={1}>Action</Text>
                <Input
                  width="150px"
                  placeholder="Filter by action"
                  value={actionFilter}
                  onChange={(e) => setActionFilter(e.target.value)}
                />
              </Box>

              <Box>
                <Text fontSize="sm" mb={1}>Decision</Text>
                <Select
                  width="120px"
                  value={decisionFilter}
                  onChange={(e) => setDecisionFilter(e.target.value)}
                >
                  <option value="all">All</option>
                  <option value="granted">Granted</option>
                  <option value="denied">Denied</option>
                </Select>
              </Box>

              <Box>
                <Text fontSize="sm" mb={1}>Date From</Text>
                <Input
                  type="datetime-local"
                  value={dateFromFilter}
                  onChange={(e) => setDateFromFilter(e.target.value)}
                />
              </Box>

              <Box>
                <Text fontSize="sm" mb={1}>Date To</Text>
                <Input
                  type="datetime-local"
                  value={dateToFilter}
                  onChange={(e) => setDateToFilter(e.target.value)}
                />
              </Box>

              <Button
                colorScheme="green"
                onClick={fetchTrustLogs}
                isDisabled={loading}
              >
                Apply Filters
              </Button>
            </Flex>
          </Box>

          {/* Results Section */}
          {error && (
            <Box mb={4} p={3} bg="red.100" color="red.700" borderRadius="md">
              {error}
            </Box>
          )}

          <TableContainer>
            <Table variant="simple">
              <Thead>
                <Tr>
                  <Th onClick={() => handleSort('id')} cursor="pointer">
                    ID {sortBy === 'id' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('username')} cursor="pointer">
                    User {sortBy === 'username' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('resource')} cursor="pointer">
                    Resource {sortBy === 'resource' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('action')} cursor="pointer">
                    Action {sortBy === 'action' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('ipAddress')} cursor="pointer">
                    IP Address {sortBy === 'ipAddress' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('trustScore')} cursor="pointer">
                    Trust Score {sortBy === 'trustScore' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th onClick={() => handleSort('decisionResult')} cursor="pointer">
                    Decision {sortBy === 'decisionResult' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                  <Th>Reason</Th>
                  <Th onClick={() => handleSort('timestamp')} cursor="pointer">
                    Timestamp {sortBy === 'timestamp' && (sortDir === 'asc' ? ' ↑' : ' ↓')}
                  </Th>
                </Tr>
              </Thead>
              <Tbody>
                {loading && (!trustLogs || trustLogs.length === 0) ? (
                  // Skeleton loading rows when first loading
                  Array.from({ length: 5 }).map((_, index) => (
                    <Tr key={index}>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                      <Td><Skeleton height="20px" /></Td>
                    </Tr>
                  ))
                ) : (!trustLogs || trustLogs.length === 0) ? (
                  <Tr>
                    <Td colSpan={9} textAlign="center" py={8}>
                      <Text>No trust logs found</Text>
                    </Td>
                  </Tr>
                ) : (
                  trustLogs.map((log) => (
                    <Tr
                      key={log.id}
                      _hover={{ bg: tableRowHover }}
                    >
                      <Td>{log.id}</Td>
                      <Td>
                        {log.userId ? `ID: ${log.userId}` : 'N/A'}<br/>
                        <Text fontSize="sm" color="gray.500">{log.username}</Text>
                      </Td>
                      <Td>{log.resource}</Td>
                      <Td>{log.action}</Td>
                      <Td>{log.ipAddress}</Td>
                      <Td>
                        <Badge colorScheme={log.trustScore >= 0.7 ? 'green' : log.trustScore >= 0.4 ? 'yellow' : 'red'}>
                          {(log.trustScore * 100).toFixed(2)}%
                        </Badge>
                      </Td>
                      <Td>
                        <Badge colorScheme={log.decisionResult ? 'green' : 'red'}>
                          {log.decisionResult ? 'Granted' : 'Denied'}
                        </Badge>
                      </Td>
                      <Td>
                        <Tooltip label={log.reason}>
                          <Text noOfLines={1} maxWidth="200px">{log.reason}</Text>
                        </Tooltip>
                      </Td>
                      <Td>{new Date(log.timestamp).toLocaleString()}</Td>
                    </Tr>
                  ))
                )}
              </Tbody>
            </Table>
          </TableContainer>

          {/* Pagination Controls */}
          <Flex justify="space-between" align="center" mt={4}>
            <Flex align="center" gap={2}>
              <Text fontSize="sm">Items per page:</Text>
              <Select
                width="auto"
                value={pageSize}
                onChange={handlePageSizeChange}
                size="sm"
              >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="20">20</option>
                <option value="50">50</option>
              </Select>
            </Flex>

            <Text fontSize="sm">
              Showing {totalElements > 0 ? (currentPage * pageSize) + 1 : 0}-{Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements} trust decision{totalElements !== 1 ? 's' : ''}
            </Text>

            {totalPages > 1 && (
              <Flex gap={2} align="center">
                <IconButton
                  aria-label="Previous page"
                  icon={<FaChevronLeft />}
                  onClick={() => handlePageChange(currentPage - 1)}
                  isDisabled={currentPage === 0}
                  size="sm"
                />

                {/* Page number buttons */}
                {(() => {
                  const pageButtons = [];
                  const startPage = Math.max(0, Math.min(currentPage - 2, totalPages - 5));
                  const endPage = Math.min(startPage + 4, totalPages - 1);

                  if (startPage > 0) {
                    pageButtons.push(
                      <Button
                        key={0}
                        onClick={() => handlePageChange(0)}
                        variant={currentPage === 0 ? "solid" : "outline"}
                        size="sm"
                      >
                        1
                      </Button>
                    );

                    if (startPage > 1) {
                      pageButtons.push(
                        <Box key="ellipsis-start" px={2}>
                          <FaEllipsisH />
                        </Box>
                      );
                    }
                  }

                  for (let i = startPage; i <= endPage; i++) {
                    pageButtons.push(
                      <Button
                        key={i}
                        onClick={() => handlePageChange(i)}
                        variant={currentPage === i ? "solid" : "outline"}
                        size="sm"
                      >
                        {i + 1}
                      </Button>
                    );
                  }

                  if (endPage < totalPages - 1) {
                    if (endPage < totalPages - 2) {
                      pageButtons.push(
                        <Box key="ellipsis-end" px={2}>
                          <FaEllipsisH />
                        </Box>
                      );
                    }

                    pageButtons.push(
                      <Button
                        key={totalPages - 1}
                        onClick={() => handlePageChange(totalPages - 1)}
                        variant={currentPage === totalPages - 1 ? "solid" : "outline"}
                        size="sm"
                      >
                        {totalPages}
                      </Button>
                    );
                  }

                  return pageButtons;
                })()}

                <IconButton
                  aria-label="Next page"
                  icon={<FaChevronRight />}
                  onClick={() => handlePageChange(currentPage + 1)}
                  isDisabled={currentPage === totalPages - 1}
                  size="sm"
                />
              </Flex>
            )}
          </Flex>
        </CardBody>
      </Card>
    </Box>
  );
};

export default TrustLogsPage;