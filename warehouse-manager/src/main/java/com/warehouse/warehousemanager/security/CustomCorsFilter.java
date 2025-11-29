package com.warehouse.warehousemanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * CORS Filter to ensure CORS headers are set on all responses, including redirects
 * This filter runs early to handle CORS before any redirects occur
 */
@Component("customCorsFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomCorsFilter extends OncePerRequestFilter {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://warehouse-frontend.localhost:3080",
        "http://warehouse-frontend.localhost:3080",
        "http://localhost:3000",
        "http://localhost:5173",
        "http://127.0.0.1:3000",
        "http://127.0.0.1:5173"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain)
            throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        // System.out.println("CORS Filter processing request: " + request.getMethod() + " " + request.getRequestURI() + ", Origin: " + origin);
        
        // Always set CORS headers if origin is present (even if not in allowed list, we'll be more permissive)
        // This ensures CORS headers are set on redirects too
        if (origin != null) {
            // Check if origin is explicitly allowed
            if (isOriginAllowed(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            } else {
                // For development, allow any localhost origin
                if (origin.contains("localhost") || origin.contains("127.0.0.1") || origin.contains("warehouse-frontend")) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                }
            }
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Type, Access-Control-Allow-Origin");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        
        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isOriginAllowed(String origin) {
        if (origin == null) return false;
        // Check exact match or if origin contains the allowed domain
        for (String allowed : ALLOWED_ORIGINS) {
            if (origin.equals(allowed)) {
                return true;
            }
            // Also allow if origin matches the domain (without port check for flexibility)
            String allowedDomain = allowed.replace("http://", "").replace("https://", "").split(":")[0];
            String originDomain = origin.replace("http://", "").replace("https://", "").split(":")[0];
            if (originDomain.equals(allowedDomain)) {
                return true;
            }
        }
        return false;
    }
}

