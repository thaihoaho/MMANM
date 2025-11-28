package com.warehouse.warehousemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Debug controller to inspect Teleport identity headers
 * This helps verify that Teleport is forwarding identity correctly
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class TeleportDebugController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/headers")
    public ResponseEntity<Map<String, Object>> getHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        
        // Teleport identity headers
        headers.put("X-Forwarded-User", request.getHeader("X-Forwarded-User"));
        headers.put("X-Teleport-User", request.getHeader("X-Teleport-User"));
        headers.put("X-Remote-User", request.getHeader("X-Remote-User"));
        headers.put("X-Forwarded-Email", request.getHeader("X-Forwarded-Email"));
        headers.put("X-Teleport-Email", request.getHeader("X-Teleport-Email"));
        headers.put("X-Forwarded-Groups", request.getHeader("X-Forwarded-Groups"));
        headers.put("X-Teleport-Groups", request.getHeader("X-Teleport-Groups"));
        
        // Teleport JWT assertion (most important for ZTA)
        String jwtAssertion = request.getHeader("teleport-jwt-assertion");
        headers.put("teleport-jwt-assertion", jwtAssertion != null ? "PRESENT (length: " + jwtAssertion.length() + ")" : "NOT PRESENT");
        
        // Standard forwarded headers
        headers.put("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        headers.put("X-Forwarded-Host", request.getHeader("X-Forwarded-Host"));
        headers.put("X-Forwarded-Proto", request.getHeader("X-Forwarded-Proto"));
        
        // Authentication info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> authInfo = new HashMap<>();
        if (auth != null) {
            authInfo.put("name", auth.getName());
            authInfo.put("authenticated", auth.isAuthenticated());
            authInfo.put("authorities", auth.getAuthorities().toString());
            authInfo.put("principal", auth.getPrincipal().toString());
        }
        headers.put("authentication", authInfo);
        
        // All headers (for debugging)
        Map<String, String> allHeaders = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(name -> {
            allHeaders.put(name, request.getHeader(name));
        });
        headers.put("allHeaders", allHeaders);
        
        return ResponseEntity.ok(headers);
    }

    @GetMapping("/identity")
    public ResponseEntity<Map<String, Object>> getIdentity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> identity = new HashMap<>();
        
        if (auth != null) {
            identity.put("username", auth.getName());
            identity.put("authenticated", auth.isAuthenticated());
            identity.put("authorities", auth.getAuthorities());
            identity.put("principal", auth.getPrincipal().getClass().getName());
        } else {
            identity.put("authenticated", false);
            identity.put("message", "No authentication found");
        }
        
        return ResponseEntity.ok(identity);
    }

    /**
     * Get detailed certificate/credential information for visualization
     * This endpoint shows the short-lived credentials from Teleport
     */
    @GetMapping("/certificate")
    public ResponseEntity<Map<String, Object>> getCertificateInfo(HttpServletRequest request) {
        Map<String, Object> certInfo = new HashMap<>();
        
        String jwtAssertion = request.getHeader("teleport-jwt-assertion");
        
        if (jwtAssertion != null && !jwtAssertion.isEmpty()) {
            try {
                // Decode JWT to extract certificate information
                Map<String, Object> jwtData = decodeJwt(jwtAssertion);
                
                certInfo.put("hasCertificate", true);
                certInfo.put("type", "Teleport JWT Certificate");
                
                // Extract timing information (short-lived credentials)
                Long iat = getLongValue(jwtData, "iat"); // Issued At
                Long exp = getLongValue(jwtData, "exp"); // Expiration
                Long nbf = getLongValue(jwtData, "nbf"); // Not Before
                
                if (iat != null) {
                    certInfo.put("issuedAt", formatTimestamp(iat));
                    certInfo.put("issuedAtEpoch", iat);
                }
                if (exp != null) {
                    certInfo.put("expiresAt", formatTimestamp(exp));
                    certInfo.put("expiresAtEpoch", exp);
                    
                    // Calculate remaining time
                    long now = Instant.now().getEpochSecond();
                    long remainingSeconds = exp - now;
                    certInfo.put("remainingSeconds", remainingSeconds);
                    certInfo.put("remainingFormatted", formatDuration(remainingSeconds));
                    certInfo.put("isExpired", remainingSeconds <= 0);
                    
                    // Calculate certificate lifetime (TTL)
                    if (iat != null) {
                        long ttl = exp - iat;
                        certInfo.put("ttlSeconds", ttl);
                        certInfo.put("ttlFormatted", formatDuration(ttl));
                    }
                }
                if (nbf != null) {
                    certInfo.put("notBefore", formatTimestamp(nbf));
                }
                
                // Extract identity information
                certInfo.put("subject", jwtData.get("sub"));
                certInfo.put("issuer", jwtData.get("iss"));
                certInfo.put("audience", jwtData.get("aud"));
                certInfo.put("username", jwtData.getOrDefault("sub", jwtData.get("username")));
                certInfo.put("email", jwtData.get("email"));
                certInfo.put("roles", jwtData.get("roles"));
                
                // Zero Trust indicators
                certInfo.put("zeroTrustEnabled", true);
                certInfo.put("credentialType", "SHORT_LIVED");
                certInfo.put("rotationEnabled", true);
                
            } catch (Exception e) {
                certInfo.put("hasCertificate", false);
                certInfo.put("error", "Failed to decode certificate: " + e.getMessage());
            }
        } else {
            // No Teleport JWT, check if using traditional auth
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                certInfo.put("hasCertificate", false);
                certInfo.put("type", "Traditional JWT Token");
                certInfo.put("username", auth.getName());
                certInfo.put("zeroTrustEnabled", false);
                certInfo.put("credentialType", "TRADITIONAL");
                certInfo.put("rotationEnabled", false);
                certInfo.put("message", "Using traditional authentication, not Teleport short-lived credentials");
            } else {
                certInfo.put("hasCertificate", false);
                certInfo.put("message", "No Teleport certificate found. Access via Teleport proxy to see certificate info.");
            }
        }
        
        return ResponseEntity.ok(certInfo);
    }

    private Map<String, Object> decodeJwt(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        return objectMapper.readValue(payload, Map.class);
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof Double) return ((Double) value).longValue();
        return null;
    }

    private String formatTimestamp(long epochSeconds) {
        return Instant.ofEpochSecond(epochSeconds)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
    }

    private String formatDuration(long seconds) {
        if (seconds < 0) return "Expired";
        if (seconds < 60) return seconds + " seconds";
        if (seconds < 3600) return (seconds / 60) + " minutes " + (seconds % 60) + " seconds";
        if (seconds < 86400) return (seconds / 3600) + " hours " + ((seconds % 3600) / 60) + " minutes";
        return (seconds / 86400) + " days " + ((seconds % 86400) / 3600) + " hours";
    }
}

