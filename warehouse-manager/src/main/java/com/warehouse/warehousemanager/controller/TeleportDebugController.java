package com.warehouse.warehousemanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Debug controller to inspect Teleport identity headers
 * This helps verify that Teleport is forwarding identity correctly
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class TeleportDebugController {

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
}

