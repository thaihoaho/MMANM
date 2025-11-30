package com.warehouse.warehousemanager.security.policy;

import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PolicyEnforcementPoint {

    @Autowired
    private PolicyDecisionPoint policyDecisionPoint;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private com.warehouse.warehousemanager.service.UserService userService;

    public boolean checkAccess(String resource, String action, HttpServletRequest request) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        com.warehouse.warehousemanager.entity.User user = null;
        try {
            // Load thông tin user từ database thông qua service để lấy entity User
            user = userService.findByUsername(username).orElse(null);
        } catch (Exception e) {
            System.err.println("Error looking up user for policy evaluation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        if (user == null) {
            System.err.println("User not found in database: " + username);
            return false;
        }

        // Create a policy request with context information
        PolicyRequest policyRequest = new PolicyRequest();
        policyRequest.setUser(user);
        policyRequest.setResource(resource);
        policyRequest.setAction(action);
        policyRequest.setRequest(request);

        // Extract context information
        policyRequest.setIpAddress(request.getRemoteAddr());

        // Get current hour of day
        java.time.LocalTime now = java.time.LocalTime.now();
        policyRequest.setHourOfDay(now.getHour());

        // Mock risk score (in a real system, this would come from a risk assessment service)
        policyRequest.setRiskScore(calculateRiskScore(request, user));

        // Evaluate the policy
        boolean decision = policyDecisionPoint.evaluate(policyRequest);

        // The decision is already logged in the PolicyDecisionPoint
        return decision;
    }

    private double calculateRiskScore(HttpServletRequest request, User user) {
        // This is a hardcoded risk calculation based on various factors
        double score = 0.0;

        // Check if the request is coming from localhost (low risk)
        String clientIP = request.getRemoteAddr();
        if (clientIP.equals("127.0.0.1") || clientIP.equals("0:0:0:0:0:0:0:1")) {
            score -= 0.1; // Reduce risk for localhost
            System.err.println("Access from localhost detected, reducing risk score.");
        }

        // Check for suspicious IP patterns (high risk)
        if (clientIP.startsWith("192.168.") || clientIP.startsWith("10.") || clientIP.startsWith("172.")) {
            // Internal IP ranges - moderate risk depending on context
            score += 0.1;
            System.err.println("Access from internal IP range detected, increasing risk score.");
        } else if (clientIP.contains(":") && !clientIP.startsWith("fe80")) {
            // IPv6 address (not link-local) - might be legitimate or suspicious
            score += 0.05;
            System.err.println("Access from IPv6 address detected, slightly increasing risk score.");
        }

        // Check time-based risk (access during unusual hours)
        java.time.LocalTime now = java.time.LocalTime.now();
        int hour = now.getHour();
        if (hour < 6 || hour > 22) { // Between 10 PM and 6 AM
            score += 0.2; // Higher risk during nighttime hours
            System.err.println("Access during unusual hours detected, increasing risk score.");
        }

        // Check user role-based risk
        if (user.getRole() == User.Role.USER) {
            score += 0.1; // Regular users have slightly higher risk than admin
            System.err.println("User role is USER, increasing risk score.");
        } else if (user.getRole() == User.Role.ADMIN) {
            score += 0.05; // Admins have power, so slightly higher risk
            System.err.println("User role is ADMIN, slightly increasing risk score.");
        }

        // Check for sensitive resource access
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/users") || requestURI.contains("/admin")) {
            score += 0.3; // Higher risk for sensitive operations
            System.err.println("Accessing sensitive resource detected, increasing risk score.");
        } else if (requestURI.contains("/products") || requestURI.contains("/imports") || requestURI.contains("/exports")) {
            score += 0.15; // Medium risk for warehouse operations
            System.err.println("Accessing warehouse resource detected, moderately increasing risk score.");
        }

        // Check HTTP method risk
        String method = request.getMethod();
        if ("DELETE".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            score += 0.2; // Higher risk for destructive operations
            System.err.println("Destructive HTTP method detected, increasing risk score.");
        } else if ("POST".equalsIgnoreCase(method)) {
            score += 0.1; // Medium risk for create operations
            System.err.println("Create HTTP method detected, moderately increasing risk score.");
        }

        // Apply boundaries to ensure score stays within 0.0 - 1.0
        score = Math.max(0.0, Math.min(1.0, score));

        return score;
    }
}