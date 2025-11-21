import {
  Box,
  Flex,
  Text,
  IconButton,
  Button,
  Stack,
  Collapse,
  Icon,
  Link,
  Popover,
  PopoverTrigger,
  PopoverContent,
  useColorModeValue,
  useDisclosure,
} from '@chakra-ui/react';
import {
  HamburgerIcon,
  CloseIcon,
  ChevronDownIcon,
  ChevronRightIcon,
} from '@chakra-ui/icons';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import useAuthStore from '../store/auth';

export default function Layout() {
  const { isOpen, onToggle } = useDisclosure();
  const { user, logout } = useAuthStore();

  const handleLogout = () => logout();

  return (
    <Box>
      <Flex
        bg={useColorModeValue('white', 'gray.800')}
        color={useColorModeValue('gray.600', 'white')}
        minH={'60px'}
        py={4}
        px={8}
        borderBottomWidth={1}
        borderColor={useColorModeValue('gray.200', 'gray.900')}
        align="center"
        justify="space-between"
        w="100%"
      >
        {/* LEFT: LOGO */}
        <Flex flex="0 0 auto" align="center">
          <Text
            as={RouterLink}
            to="/"
            fontFamily="heading"
            color={useColorModeValue('gray.800', 'white')}
            fontSize="xl"
            fontWeight="bold"
          >
            Warehouse Manager
          </Text>
        </Flex>

        {/* CENTER: NAV */}
        <Flex
          flex="1"
          justify="center"
          display={{ base: 'none', md: 'flex' }}
        >
          <DesktopNav />
        </Flex>

        {/* RIGHT: USER */}
        <Flex
          flex="0 0 auto"
          align="center"
          display={{ base: 'none', md: 'flex' }}
        >
          <Text
            mr={4}
            fontWeight="bold"
            color="blue.600"
            bg="blue.50"
            px={3}
            py={1}
            borderRadius="md"
            border="1px solid"
            borderColor="blue.200"
          >
            {user?.username} ({user?.role})
          </Text>
          <Button size="sm" colorScheme="red" onClick={logout}>
            Logout
          </Button>
        </Flex>
      </Flex>

      <Collapse in={isOpen}>
        <MobileNav />
      </Collapse>
    </Box>
  );
}

/* ========================= NAV COMPONENTS ========================= */

const DesktopNav = () => {
  const { user } = useAuthStore();
  const location = useLocation();
  const linkColor = useColorModeValue('gray.600', 'gray.200');
  const linkHoverColor = useColorModeValue('gray.800', 'white');

  const navItems = user?.role === 'ADMIN' ? ADMIN_NAV_ITEMS : NAV_ITEMS;

  return (
    <Flex justify="center" gap={6}>

      {navItems.map((navItem) => {
        const active = location.pathname === navItem.href;

        return (
          <Box key={navItem.label}>
            <Popover trigger="hover" placement="bottom-start">
              <PopoverTrigger>
                <Link
                  as={RouterLink}
                  to={navItem.href}
                  p={2}
                  fontSize="sm"
                  fontWeight={active ? 700 : 500}
                  color={active ? "blue.600" : linkColor}
                  _hover={{ textDecoration: 'none', color: linkHoverColor }}
                >
                  {navItem.label}
                </Link>
              </PopoverTrigger>

              {navItem.children && (
                <PopoverContent border={0} boxShadow="xl" p={4} rounded="xl" minW="sm">
                  <Stack>
                    {navItem.children.map((child) => (
                      <DesktopSubNav key={child.label} {...child} />
                    ))}
                  </Stack>
                </PopoverContent>
              )}
            </Popover>
          </Box>
        );
      })}
    </Flex>
  );
};

const DesktopSubNav = ({ label, href, subLabel }: NavItem) => (
  <Link
    as={RouterLink}
    to={href}
    role="group"
    display="block"
    p={2}
    rounded="md"
    _hover={{ bg: useColorModeValue('pink.50', 'gray.900') }}
  >
    <Stack direction="row" align="center">
      <Box>
        <Text fontWeight={500} _groupHover={{ color: 'pink.600' }}>
          {label}
        </Text>
        {subLabel && <Text fontSize="sm">{subLabel}</Text>}
      </Box>
      <Flex
        align="center"
        justify="flex-end"
        flex={1}
        opacity={0}
        transform="translateX(-10px)"
        transition="all .3s"
        _groupHover={{ opacity: 1, transform: 'translateX(0)' }}
      >
        <Icon as={ChevronRightIcon} w={5} h={5} />
      </Flex>
    </Stack>
  </Link>
);

const MobileNav = () => {
  const { user } = useAuthStore();
  const navItems = user?.role === 'ADMIN' ? ADMIN_NAV_ITEMS : NAV_ITEMS;

  return (
    <Stack bg={useColorModeValue('white', 'gray.800')} p={4} display={{ md: 'none' }}>
      {navItems.map((navItem) => (
        <MobileNavItem key={navItem.label} {...navItem} />
      ))}
    </Stack>
  );
};

const MobileNavItem = ({ label, children, href }: NavItem) => {
  const { isOpen, onToggle } = useDisclosure();

  return (
    <Stack spacing={4}>
      <Flex py={2} justify="space-between" align="center">
        <Link as={RouterLink} to={href} fontWeight={600}>
          {label}
        </Link>

        {children && (
          <IconButton
            aria-label="Expand Menu"
            variant="ghost"
            onClick={onToggle}
            icon={<ChevronDownIcon transform={isOpen ? 'rotate(180deg)' : 'rotate(0deg)'} />}
          />
        )}
      </Flex>

      <Collapse in={isOpen}>
        <Stack pl={4} borderLeftWidth={1} borderColor="gray.200" align="start">
          {children?.map((child) => (
            <Link key={child.label} py={2} as={RouterLink} to={child.href}>
              {child.label}
            </Link>
          ))}
        </Stack>
      </Collapse>
    </Stack>
  );
};

/* ========================= TYPES & NAV ITEMS ========================= */

interface NavItem {
  label: string;
  subLabel?: string;
  children?: Array<NavItem>;
  href: string;
}

const NAV_ITEMS: Array<NavItem> = [
  { label: 'Dashboard', href: '/dashboard' },
  { label: 'Products', href: '/products' },
  { label: 'Imports', href: '/imports' },
  { label: 'Exports', href: '/exports' },
];

const ADMIN_NAV_ITEMS: Array<NavItem> = [
  { label: 'Dashboard', href: '/dashboard' },
  { label: 'Users', href: '/users' },
  { label: 'Products', href: '/products' },
  { label: 'Imports', href: '/imports' },
  { label: 'Exports', href: '/exports' },
];
