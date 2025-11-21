package com.example.warehouse_management.pep;

import com.example.warehouse_management.entity.User;
import com.example.warehouse_management.pdp.PolicyDecisionPoint;
import com.example.warehouse_management.pdp.model.Decision;
import com.example.warehouse_management.pdp.model.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PolicyEnforcementPoint {

    @Autowired
    private PolicyDecisionPoint pdp;

    public boolean checkPermission(String resource, String action) {
        return checkPermission(resource, action, null);
    }

    public boolean checkPermission(String resource, String action, Object resourceObject) {
        // Get the current user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false; // Not authenticated, deny access
        }

        // Assuming the principal is a User object
        User user = null;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }

        // Create request context
        RequestContext context = new RequestContext(user, resource, action, resourceObject);

        // Evaluate the policy decision
        Decision decision = pdp.evaluate(context);

        return decision.isPermitted();
    }

    public Decision getDetailedDecision(String resource, String action, Object resourceObject) {
        // Get the current user from Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return new Decision(false, "User not authenticated", null); // Not authenticated, deny access
        }

        // Assuming the principal is a User object
        User user = null;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        }

        // Create request context
        RequestContext context = new RequestContext(user, resource, action, resourceObject);

        // Evaluate the policy decision
        return pdp.evaluate(context);
    }
}