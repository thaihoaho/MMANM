package com.warehouse.warehousemanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Filter to extract Teleport identity from headers and create authentication
 * This implements Zero Trust Architecture by trusting identity from Teleport proxy
 */
@Component
public class TeleportIdentityFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // Priority 1: Check for Teleport JWT token (teleport-jwt-assertion)
        String teleportJwt = request.getHeader("teleport-jwt-assertion");
        String teleportUser = null;
        
        if (teleportJwt != null && !teleportJwt.isEmpty()) {
            try {
                // Decode JWT token to extract identity
                TeleportIdentity identity = decodeTeleportJwt(teleportJwt);
                teleportUser = identity.getUsername();
                
                // Create authentication from JWT
                Authentication authentication = createTeleportAuthenticationFromJwt(request, identity);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug(String.format("Teleport JWT authenticated: user=%s, email=%s, roles=%s", 
                    identity.getUsername(),
                    identity.getEmail(),
                    identity.getRoles()));
            } catch (Exception e) {
                logger.warn("Failed to decode Teleport JWT, falling back to headers", e);
                // Fallback to header-based extraction
                teleportUser = extractTeleportUser(request);
            }
        } else {
            // Priority 2: Try to extract from headers
            teleportUser = extractTeleportUser(request);
        }
        
        // If we have a user from headers (and JWT decoding failed or not present)
        if (teleportUser != null && !teleportUser.isEmpty() && 
            SecurityContextHolder.getContext().getAuthentication() == null) {
            // Create authentication from headers
            Authentication authentication = createTeleportAuthentication(request, teleportUser);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            logger.debug(String.format("Teleport identity authenticated from headers: user=%s", teleportUser));
        }
        // If no Teleport identity found, let JwtAuthenticationFilter handle it
        
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to map Teleport roles to System roles
     * Maps 'access' role from Teleport to 'ADMIN' role in System
     */
    private String mapTeleportRole(String role) {
        if (role == null) return "USER";
        String normalizedRole = role.trim();
        // Map 'access' role from Teleport to 'ADMIN' role in System
        if (normalizedRole.equalsIgnoreCase("access") || normalizedRole.equalsIgnoreCase("admin")) {
            return "ADMIN";
        }
        return normalizedRole.toUpperCase();
    }

    /**
     * Decode Teleport JWT token to extract identity
     * Note: We decode without verification since we trust Teleport proxy
     */
    private TeleportIdentity decodeTeleportJwt(String jwtToken) throws Exception {
        try {
            // Split JWT into parts: header.payload.signature
            String[] parts = jwtToken.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid JWT format");
            }
            
            // Decode payload (base64url)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse JSON payload using Jackson
            Map<String, Object> json = objectMapper.readValue(payload, Map.class);
            
            TeleportIdentity identity = new TeleportIdentity();
            // Extract username from 'sub' (subject) or 'username' claim
            String username = (String) json.getOrDefault("sub", json.getOrDefault("username", "teleport-user"));
            identity.setUsername(username);
            identity.setEmail((String) json.getOrDefault("email", ""));
            
            // Extract roles from JWT
            Object rolesObj = json.get("roles");
            List<String> roles = new ArrayList<>();
            if (rolesObj != null) {
                if (rolesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> rolesList = (List<Object>) rolesObj;
                    for (Object role : rolesList) {
                        roles.add(role.toString());
                    }
                } else if (rolesObj instanceof String) {
                    roles.add((String) rolesObj);
                }
            }
            identity.setRoles(roles);
            
            return identity;
        } catch (Exception e) {
            logger.error("Error decoding Teleport JWT", e);
            throw new RuntimeException("Failed to decode Teleport JWT", e);
        }
    }
    
    /**
     * Create authentication from Teleport JWT identity
     */
    private Authentication createTeleportAuthenticationFromJwt(
            HttpServletRequest request, TeleportIdentity identity) {
        try {
            org.springframework.security.core.userdetails.UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(identity.getUsername());
            } catch (Exception e) {
                // User doesn't exist in database, create from JWT identity
                logger.warn(String.format("User %s not found in database, creating from Teleport JWT", 
                    identity.getUsername()), e);
                userDetails = createUserFromTeleportIdentity(identity);
            }
            
            // Extract authorities from JWT roles
            List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
            if (identity.getRoles() != null) {
                for (String role : identity.getRoles()) {
                    String mappedRole = mapTeleportRole(role);
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + mappedRole));
                }
            }
            // Add existing user authorities
            if (userDetails != null) {
                authorities.addAll(userDetails.getAuthorities());
            }
            // Default role if no roles specified
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
            
            return new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, 
                authorities
            );
        } catch (Exception e) {
            logger.error("Error creating authentication from JWT", e);
            // Fallback
            List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            org.springframework.security.core.userdetails.User user = 
                new org.springframework.security.core.userdetails.User(
                    identity.getUsername(), 
                    "", 
                    authorities
                );
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }
    }
    
    /**
     * Create user details from Teleport JWT identity
     */
    private org.springframework.security.core.userdetails.UserDetails createUserFromTeleportIdentity(
            TeleportIdentity identity) {
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        if (identity.getRoles() != null) {
            for (String role : identity.getRoles()) {
                String mappedRole = mapTeleportRole(role);
                authorities.add(new SimpleGrantedAuthority("ROLE_" + mappedRole));
            }
        }
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(identity.getUsername())
            .password("")
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
    
    /**
     * Extract Teleport user from headers
     * Teleport can send identity in different header formats
     */
    private String extractTeleportUser(HttpServletRequest request) {
        // Try different header formats that Teleport might use
        String user = request.getHeader("X-Forwarded-User");
        if (user == null || user.isEmpty()) {
            user = request.getHeader("X-Teleport-User");
        }
        if (user == null || user.isEmpty()) {
            user = request.getHeader("X-Remote-User");
        }
        return user;
    }
    
    /**
     * Inner class to hold Teleport identity from JWT
     */
    private static class TeleportIdentity {
        private String username;
        private String email;
        private List<String> roles;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }

    /**
     * Create Spring Security authentication from Teleport identity
     */
    private Authentication createTeleportAuthentication(HttpServletRequest request, String teleportUser) {
        try {
            // Try to load user from database by username
            // If user doesn't exist, we'll create a basic authentication
            org.springframework.security.core.userdetails.UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(teleportUser);
            } catch (Exception e) {
                // User doesn't exist in database, create a basic user from Teleport identity
                logger.warn(String.format("User %s not found in database, creating from Teleport identity", teleportUser), e);
                userDetails = createUserFromTeleportIdentity(request, teleportUser);
            }

            // Extract groups/roles from Teleport headers
            Collection<org.springframework.security.core.GrantedAuthority> authorities = 
                extractAuthorities(request, userDetails);

            return new UsernamePasswordAuthenticationToken(
                userDetails, 
                null, // No credentials needed (already authenticated by Teleport)
                authorities
            );
        } catch (Exception e) {
            logger.error("Error creating Teleport authentication", e);
            // Fallback: create basic authentication
            return createBasicTeleportAuthentication(request, teleportUser);
        }
    }

    /**
     * Create user details from Teleport identity when user doesn't exist in database
     */
    private org.springframework.security.core.userdetails.UserDetails createUserFromTeleportIdentity(
            HttpServletRequest request, String username) {
        String email = request.getHeader("X-Forwarded-Email");
        if (email == null) {
            email = request.getHeader("X-Teleport-Email");
        }
        
        Collection<org.springframework.security.core.GrantedAuthority> authoritiesCollection = 
            extractAuthorities(request, null);
        List<org.springframework.security.core.GrantedAuthority> authorities = 
            new ArrayList<>(authoritiesCollection);
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(username)
            .password("") // No password needed (authenticated by Teleport)
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }

    /**
     * Extract authorities (roles/groups) from Teleport headers
     */
    private Collection<org.springframework.security.core.GrantedAuthority> extractAuthorities(
            HttpServletRequest request, 
            org.springframework.security.core.userdetails.UserDetails existingUser) {
        
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        
        // If user exists, use their existing authorities
        if (existingUser != null) {
            authorities.addAll(existingUser.getAuthorities());
        }
        
        // Add authorities from Teleport groups header
        String groupsHeader = request.getHeader("X-Forwarded-Groups");
        if (groupsHeader == null) {
            groupsHeader = request.getHeader("X-Teleport-Groups");
        }
        
        if (groupsHeader != null && !groupsHeader.isEmpty()) {
            String[] groups = groupsHeader.split(",");
            for (String group : groups) {
                group = group.trim();
                if (!group.isEmpty()) {
                    String mappedRole = mapTeleportRole(group);
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + mappedRole));
                }
            }
        }
        
        // Default role if no groups specified
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }

    /**
     * Fallback: create basic authentication from Teleport identity
     */
    private Authentication createBasicTeleportAuthentication(
            HttpServletRequest request, String username) {
        List<org.springframework.security.core.GrantedAuthority> authorities = 
            new ArrayList<>(extractAuthorities(request, null));
        
        org.springframework.security.core.userdetails.User user = 
            new org.springframework.security.core.userdetails.User(
                username, 
                "", 
                authorities
            );
        
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}

