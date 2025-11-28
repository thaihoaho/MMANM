import {
  Box,
  VStack,
  HStack,
  Text,
  Badge,
  Progress,
  Stat,
  StatLabel,
  StatNumber,
  StatHelpText,
  Icon,
  Tooltip,
  Spinner,
  Alert,
  AlertIcon,
  SimpleGrid,
  Divider,
} from '@chakra-ui/react';
import { CheckCircleIcon, WarningIcon, TimeIcon, LockIcon } from '@chakra-ui/icons';
import React, { useEffect, useState } from 'react';
import axios from '../utils/axios';
import { API_BASE_URL } from '../utils/apiConfig';

interface CertificateInfo {
  hasCertificate: boolean;
  type?: string;
  issuedAt?: string;
  issuedAtEpoch?: number;
  expiresAt?: string;
  expiresAtEpoch?: number;
  remainingSeconds?: number;
  remainingFormatted?: string;
  isExpired?: boolean;
  ttlSeconds?: number;
  ttlFormatted?: string;
  subject?: string;
  issuer?: string;
  username?: string;
  email?: string;
  roles?: string[];
  zeroTrustEnabled?: boolean;
  credentialType?: string;
  rotationEnabled?: boolean;
  message?: string;
  error?: string;
}

const CertificateStatus: React.FC = () => {
  const [certInfo, setCertInfo] = useState<CertificateInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentTime, setCurrentTime] = useState(Date.now());

  useEffect(() => {
    fetchCertificateInfo();
    
    // Update remaining time every second
    const interval = setInterval(() => {
      setCurrentTime(Date.now());
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const fetchCertificateInfo = async () => {
    try {
      setLoading(true);
      const response = await axios.get<CertificateInfo>(`${API_BASE_URL}/api/debug/certificate`);
      setCertInfo(response.data);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch certificate info');
      // Set default info when not through Teleport
      setCertInfo({
        hasCertificate: false,
        zeroTrustEnabled: false,
        credentialType: 'TRADITIONAL',
        message: 'Not accessing via Teleport proxy'
      });
    } finally {
      setLoading(false);
    }
  };

  // Calculate live remaining time
  const getRemainingTime = () => {
    if (!certInfo?.expiresAtEpoch) return null;
    const now = Math.floor(currentTime / 1000);
    const remaining = certInfo.expiresAtEpoch - now;
    return remaining;
  };

  const formatDuration = (seconds: number) => {
    if (seconds < 0) return 'Expired';
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ${seconds % 60}s`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}h ${Math.floor((seconds % 3600) / 60)}m`;
    return `${Math.floor(seconds / 86400)}d ${Math.floor((seconds % 86400) / 3600)}h`;
  };

  const getProgressValue = () => {
    if (!certInfo?.ttlSeconds || !certInfo?.expiresAtEpoch) return 0;
    const remaining = getRemainingTime() || 0;
    return Math.max(0, Math.min(100, (remaining / certInfo.ttlSeconds) * 100));
  };

  const getProgressColor = () => {
    const progress = getProgressValue();
    if (progress > 50) return 'green';
    if (progress > 20) return 'yellow';
    return 'red';
  };

  if (loading) {
    return (
      <Box p={6} bg="white" rounded="lg" boxShadow="md">
        <HStack>
          <Spinner size="sm" />
          <Text>Loading certificate status...</Text>
        </HStack>
      </Box>
    );
  }

  const remainingSeconds = getRemainingTime();

  return (
    <Box
      p={6}
      bg="white"
      rounded="lg"
      boxShadow="md"
      borderLeft="4px"
      borderLeftColor={certInfo?.hasCertificate ? 'purple.500' : 'gray.300'}
    >
      <VStack spacing={4} align="stretch">
        {/* Header */}
        <HStack justify="space-between">
          <HStack>
            <Icon as={LockIcon} color="purple.500" boxSize={5} />
            <Text fontSize="lg" fontWeight="bold" color="gray.700">
              Short-Lived Credentials
            </Text>
          </HStack>
          <Badge
            colorScheme={certInfo?.zeroTrustEnabled ? 'green' : 'gray'}
            fontSize="sm"
            px={2}
            py={1}
            rounded="md"
          >
            {certInfo?.zeroTrustEnabled ? 'Zero Trust Active' : 'Traditional Auth'}
          </Badge>
        </HStack>

        <Divider />

        {certInfo?.hasCertificate ? (
          <>
            {/* Certificate Status */}
            <SimpleGrid columns={{ base: 1, md: 3 }} spacing={4}>
              <Stat>
                <StatLabel>Certificate Type</StatLabel>
                <StatNumber fontSize="md">{certInfo.type}</StatNumber>
                <StatHelpText>
                  <Badge colorScheme="purple">{certInfo.credentialType}</Badge>
                </StatHelpText>
              </Stat>

              <Stat>
                <StatLabel>Username</StatLabel>
                <StatNumber fontSize="md">{certInfo.username || certInfo.subject}</StatNumber>
                <StatHelpText>
                  {certInfo.roles?.map((role, idx) => (
                    <Badge key={idx} colorScheme="blue" mr={1}>
                      {role}
                    </Badge>
                  ))}
                </StatHelpText>
              </Stat>

              <Stat>
                <StatLabel>
                  <HStack>
                    <Icon as={TimeIcon} />
                    <Text>Time Remaining</Text>
                  </HStack>
                </StatLabel>
                <StatNumber 
                  fontSize="md" 
                  color={remainingSeconds && remainingSeconds < 300 ? 'red.500' : 'green.500'}
                >
                  {remainingSeconds !== null ? formatDuration(remainingSeconds) : 'N/A'}
                </StatNumber>
                <StatHelpText>
                  {remainingSeconds && remainingSeconds < 60 && (
                    <Badge colorScheme="red" animation="pulse">Expiring Soon!</Badge>
                  )}
                </StatHelpText>
              </Stat>
            </SimpleGrid>

            {/* Certificate Lifetime Progress */}
            <Box>
              <HStack justify="space-between" mb={2}>
                <Text fontSize="sm" color="gray.600">Certificate Lifetime</Text>
                <Text fontSize="sm" color="gray.600">
                  TTL: {certInfo.ttlFormatted}
                </Text>
              </HStack>
              <Progress
                value={getProgressValue()}
                colorScheme={getProgressColor()}
                size="md"
                rounded="md"
                hasStripe
                isAnimated
              />
            </Box>

            {/* Timeline */}
            <Box bg="gray.50" p={4} rounded="md">
              <Text fontSize="sm" fontWeight="bold" mb={2} color="gray.700">
                🔄 Certificate Rotation Timeline
              </Text>
              <SimpleGrid columns={{ base: 1, md: 2 }} spacing={2}>
                <HStack>
                  <Icon as={CheckCircleIcon} color="green.500" />
                  <Text fontSize="sm">
                    <strong>Issued:</strong> {certInfo.issuedAt}
                  </Text>
                </HStack>
                <HStack>
                  <Icon as={certInfo.isExpired ? WarningIcon : TimeIcon} color={certInfo.isExpired ? 'red.500' : 'orange.500'} />
                  <Text fontSize="sm">
                    <strong>Expires:</strong> {certInfo.expiresAt}
                  </Text>
                </HStack>
              </SimpleGrid>
            </Box>

            {/* Zero Trust Indicators */}
            <SimpleGrid columns={{ base: 2, md: 4 }} spacing={2}>
              <Tooltip label="Certificates automatically rotate before expiration">
                <Box bg="green.50" p={2} rounded="md" textAlign="center">
                  <Icon as={CheckCircleIcon} color="green.500" />
                  <Text fontSize="xs" color="green.700">Auto-Rotation</Text>
                </Box>
              </Tooltip>
              <Tooltip label="Short-lived credentials minimize exposure window">
                <Box bg="green.50" p={2} rounded="md" textAlign="center">
                  <Icon as={CheckCircleIcon} color="green.500" />
                  <Text fontSize="xs" color="green.700">Short-Lived</Text>
                </Box>
              </Tooltip>
              <Tooltip label="Identity verified by Teleport proxy">
                <Box bg="green.50" p={2} rounded="md" textAlign="center">
                  <Icon as={CheckCircleIcon} color="green.500" />
                  <Text fontSize="xs" color="green.700">Verified Identity</Text>
                </Box>
              </Tooltip>
              <Tooltip label="Zero Trust Architecture enabled">
                <Box bg="purple.50" p={2} rounded="md" textAlign="center">
                  <Icon as={LockIcon} color="purple.500" />
                  <Text fontSize="xs" color="purple.700">Zero Trust</Text>
                </Box>
              </Tooltip>
            </SimpleGrid>
          </>
        ) : (
          <Alert status="info" rounded="md">
            <AlertIcon />
            <VStack align="start" spacing={1}>
              <Text fontWeight="bold">Not using Teleport Short-Lived Credentials</Text>
              <Text fontSize="sm">
                {certInfo?.message || 'Access the app via Teleport proxy (https://warehouse-frontend.localhost:3080) to see certificate rotation in action.'}
              </Text>
              <HStack mt={2}>
                <Badge colorScheme="gray">Traditional Authentication</Badge>
                <Badge colorScheme="yellow">No Auto-Rotation</Badge>
              </HStack>
            </VStack>
          </Alert>
        )}
      </VStack>
    </Box>
  );
};

export default CertificateStatus;

