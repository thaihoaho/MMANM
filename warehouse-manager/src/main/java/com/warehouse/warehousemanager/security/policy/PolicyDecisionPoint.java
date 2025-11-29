package com.warehouse.warehousemanager.security.policy;

import com.warehouse.warehousemanager.entity.TrustLog;
import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.service.TrustLogService;
import com.warehouse.warehousemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PolicyDecisionPoint {

    @Autowired
    private UserService userService;

    @Autowired
    private TrustLogService trustLogService;

    public boolean evaluate(PolicyRequest request) {
        User user = request.getUser();
        String resource = request.getResource();
        String action = request.getAction();
        String ipAddress = request.getIpAddress();
        double riskScore = request.getRiskScore();

        System.out.println("Starting policy evaluation for user: " + user.getUsername() +
                          ", resource: " + resource + ", action: " + action);

        // Check role-based access
        boolean roleBasedAccess = checkRoleBasedAccess(user, resource, action);
        System.out.println("Role-based access result: " + roleBasedAccess);

        // Check permission-based access
        boolean permissionBasedAccess = checkPermissionBasedAccess(user, resource, action);
        System.out.println("Permission-based access result: " + permissionBasedAccess);

        // Check context-based access (location, time, risk)
        boolean contextBasedAccess = checkContextBasedAccess(request);
        System.out.println("Context-based access result: " + contextBasedAccess);

        // All checks must pass for access to be granted
        boolean decisionResult = roleBasedAccess && permissionBasedAccess && contextBasedAccess;
        System.out.println("Overall decision result: " + decisionResult);

        // Log the trust decision - wrap in try-catch to prevent logging issues from affecting access decisions
        try {
            logTrustDecision(user, resource, action, ipAddress, riskScore, decisionResult);
        } catch (Exception e) {
            // Log the exception but don't let it affect the access decision
            System.err.println("Error logging trust decision: " + e.getMessage());
            e.printStackTrace();
        }

        return decisionResult;
    }

    private boolean checkRoleBasedAccess(User user, String resource, String action) {
        // Debug logging
        System.out.println("Checking role-based access for user: " + user.getUsername() +
                          ", role: " + user.getRole() + ", resource: " + resource);

        // Admin can access everything
        if (user.getRole() == User.Role.ADMIN) {
            System.out.println("Admin access granted for resource: " + resource);
            return true;
        }

        System.out.println("User is not admin, checking resource access for: " + resource);

        // Basic role-based rules
        switch (resource) {
            case "users":
                // Regular users can read users list but not modify
                if (action.equals("read")) {
                    return true;
                }
                // Regular users can't manage other users (create, update, delete)
                System.out.println("Denying access to users resource for non-admin");
                return false;
            case "products":
                // Both users and admins can manage products
                System.out.println("Allowing access to products resource");
                return true;
            case "imports":
            case "exports":
                // Both users and admins can manage imports/exports
                System.out.println("Allowing access to imports/exports resource");
                return true;
            default:
                System.out.println("Denying access to default resource: " + resource);
                return false;
        }
    }

    private boolean checkPermissionBasedAccess(User user, String resource, String action) {
        // Debug logging
        System.out.println("Checking permission-based access for user: " + user.getUsername() +
                          ", role: " + user.getRole() + ", resource: " + resource + ", action: " + action);

        // Admins bypass permission checks
        if (user.getRole() == User.Role.ADMIN) {
            System.out.println("Admin bypasses permission checks");
            return true;
        }

        // If user has specific permissions, check those
        if (user.getPermissions() != null) {
            String requiredPermission = resource + ":" + action;
            boolean hasPermission = user.getPermissions().contains(requiredPermission);
            System.out.println("User has required permission '" + requiredPermission + "': " + hasPermission);
            return hasPermission;
        }

        System.out.println("User has no specific permissions, returning default true");
        // Default behavior if no specific permissions
        return true;
    }

    private boolean checkContextBasedAccess(PolicyRequest request) {
        User user = request.getUser();
        String resource = request.getResource();
        String action = request.getAction();

        System.out.println("Checking context-based access for user: " + user.getUsername() +
                          ", role: " + user.getRole() + ", resource: " + resource + ", action: " + action);

        // Check location-based access (IP address)
        String userIpAddress = request.getIpAddress();
        // For now, we allow access from any IP, but this could be restricted
        boolean locationAccess = true;
        System.out.println("Location access granted: " + locationAccess);

        // Check time-based access
        int currentHour = request.getHourOfDay();
        // For example, restrict access during night hours for sensitive operations
        boolean timeAccess = true;
        // Disabled time restriction for testing purposes
        /*
        if (user.getRole() == User.Role.USER) {
            // Users can't access during night hours (0-6) for sensitive operations
            if (currentHour >= 0 && currentHour < 6) {
                timeAccess = false;
            }
        }
        System.out.println("Time access for user role " + user.getRole() + " at hour " + currentHour + ": " + timeAccess);

        // Check risk-based access
        double riskScore = request.getRiskScore();
        // If risk score is too high, deny access
        boolean riskAccess = riskScore < 0.8; // Threshold can be adjusted
        System.out.println("Risk access for score " + riskScore + " (threshold < 0.8): " + riskAccess);

        boolean result = locationAccess && timeAccess && riskAccess;
        System.out.println("Context-based access result: " + result);
        return result;
    }

    private void logTrustDecision(User user, String resource, String action, String ipAddress, double riskScore, boolean decisionResult) {
        String reason = buildReason(user, resource, action, decisionResult);

        TrustLog trustLog = new TrustLog(
            user,
            resource,
            action,
            ipAddress,
            riskScore,
            decisionResult,
            reason
        );

        trustLogService.save(trustLog);
    }

    private String buildReason(User user, String resource, String action, boolean decisionResult) {
        StringBuilder reason = new StringBuilder();

        if (user.getRole() == User.Role.ADMIN) {
            reason.append("Admin access granted");
        } else {
            reason.append("Standard ");
            reason.append(decisionResult ? "granted" : "denied");
            reason.append(" for role: ").append(user.getRole());

            // Add specific access details
            if (resource.equals("users") && action.equals("manage") && user.getRole() == User.Role.USER) {
                reason.append(" (Users cannot manage other users)");
            }
        }

        return reason.toString();
    }
}