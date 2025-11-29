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
        // This is a simplified risk calculation
        // In a real system, this would be much more complex
        double score = 0.0;

        // Example factors that could increase risk
        // - New device/IP
        // - Unusual time
        // - Suspicious patterns
        // - etc.

        // For now, returning a static score
        return 0.1; // Low risk by default
    }
}