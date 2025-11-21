package com.warehouse.warehousemanager.security.policy;

import com.warehouse.warehousemanager.entity.User;
import com.warehouse.warehousemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PolicyDecisionPoint {

    @Autowired
    private UserService userService;

    public boolean evaluate(PolicyRequest request) {
        User user = request.getUser();
        
        // Check role-based access
        boolean roleBasedAccess = checkRoleBasedAccess(user, request.getResource(), request.getAction());
        
        // Check permission-based access
        boolean permissionBasedAccess = checkPermissionBasedAccess(user, request.getResource(), request.getAction());
        
        // Check context-based access (location, time, risk)
        boolean contextBasedAccess = checkContextBasedAccess(request);
        
        // All checks must pass for access to be granted
        return roleBasedAccess && permissionBasedAccess && contextBasedAccess;
    }

    private boolean checkRoleBasedAccess(User user, String resource, String action) {
        // Admin can access everything
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        
        // Basic role-based rules
        switch (resource) {
            case "users":
                // Regular users can't manage other users
                return false;
            case "products":
                // Both users and admins can manage products
                return true;
            case "imports":
            case "exports":
                // Both users and admins can manage imports/exports
                return true;
            default:
                return false;
        }
    }

    private boolean checkPermissionBasedAccess(User user, String resource, String action) {
        // Admins bypass permission checks
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }

        // If user has specific permissions, check those
        if (user.getPermissions() != null) {
            String requiredPermission = resource + ":" + action;
            return user.getPermissions().contains(requiredPermission);
        }

        // Default behavior if no specific permissions
        return true;
    }

    private boolean checkContextBasedAccess(PolicyRequest request) {
        User user = request.getUser();
        
        // Check location-based access (IP address)
        String userIpAddress = request.getIpAddress();
        // For now, we allow access from any IP, but this could be restricted
        boolean locationAccess = true;
        
        // Check time-based access
        int currentHour = request.getHourOfDay();
        // For example, restrict access during night hours for sensitive operations
        boolean timeAccess = true;
        if (user.getRole() == User.Role.USER) {
            // Users can't access during night hours (0-6) for sensitive operations
            if (currentHour >= 0 && currentHour < 6) {
                timeAccess = false;
            }
        }
        
        // Check risk-based access
        double riskScore = request.getRiskScore();
        // If risk score is too high, deny access
        boolean riskAccess = riskScore < 0.8; // Threshold can be adjusted
        
        return locationAccess && timeAccess && riskAccess;
    }
}