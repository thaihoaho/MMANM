package com.example.warehouse_management.pdp;

import com.example.warehouse_management.pdp.model.Decision;
import com.example.warehouse_management.pdp.model.Effect;
import com.example.warehouse_management.pdp.model.Policy;
import com.example.warehouse_management.pdp.model.RequestContext;
import com.example.warehouse_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class PolicyDecisionPoint {

    @Autowired
    private PolicyRepository policyRepository;

    public Decision evaluate(RequestContext context) {
        List<Policy> applicablePolicies = policyRepository.findByResourceAndAction(context.getResource(), context.getAction());

        if (applicablePolicies.isEmpty()) {
            // No policies match the resource and action, default to deny
            return new Decision(false, "No applicable policy found", null);
        }

        // Evaluate policies in order of specificity
        for (Policy policy : applicablePolicies) {
            Decision decision = evaluatePolicy(policy, context);
            if (decision.isPermitted()) {
                return decision;
            }
        }

        // No policy granted access, default to deny
        return new Decision(false, "No policy permitted request", null);
    }

    private Decision evaluatePolicy(Policy policy, RequestContext context) {
        // Check if the subject (user role) matches
        if (!isSubjectMatch(policy, context)) {
            return new Decision(false, "Subject does not match policy", policy.getId().toString());
        }

        // Check if conditions are satisfied
        if (!areConditionsSatisfied(policy, context)) {
            return new Decision(false, "Conditions not satisfied", policy.getId().toString());
        }

        // Return the effect of the policy
        boolean permitted = policy.getEffect() == Effect.PERMIT;
        String reason = permitted ? "Policy permits request" : "Policy denies request";
        return new Decision(permitted, reason, policy.getId().toString());
    }

    private boolean isSubjectMatch(Policy policy, RequestContext context) {
        if (policy.getSubjects() == null || policy.getSubjects().isEmpty()) {
            // If no subjects specified, policy applies to all
            return true;
        }

        if (context.getUser() == null) {
            return false;
        }

        // Check if user's role matches any of the allowed subjects
        String userRole = context.getUser().getRole();
        return policy.getSubjects().contains(userRole) || policy.getSubjects().contains("ANY") || 
               policy.getSubjects().contains("USER"); // USER role would match for authenticated users
    }

    private boolean areConditionsSatisfied(Policy policy, RequestContext context) {
        if (policy.getConditions() == null || policy.getConditions().isEmpty()) {
            // If no conditions specified, all satisfied
            return true;
        }

        for (String condition : policy.getConditions()) {
            if (!evaluateCondition(condition, context)) {
                return false;
            }
        }

        return true;
    }

    private boolean evaluateCondition(String condition, RequestContext context) {
        // This is a simplified condition evaluator
        // In a real implementation, you would have more complex condition evaluation

        if (condition.contains(":")) {
            String[] parts = condition.split(":", 2);
            String conditionType = parts[0];
            String conditionValue = parts[1];

            switch (conditionType) {
                case "quantity":
                    // Example: quantity:<500 - allows operations when quantity is less than 500
                    return evaluateQuantityCondition(conditionValue, context);
                case "quantity>":
                    return evaluateQuantityCondition(conditionValue, context, "greater");
                case "quantity>=":
                    return evaluateQuantityCondition(conditionValue, context, "greater_equal");
                case "quantity<":
                    return evaluateQuantityCondition(conditionValue, context, "less");
                case "quantity<=":
                    return evaluateQuantityCondition(conditionValue, context, "less_equal");
                case "quantity==":
                    return evaluateQuantityCondition(conditionValue, context, "equal");
                case "quantity!=":
                    return evaluateQuantityCondition(conditionValue, context, "not_equal");
                case "role":
                    // Example: role:admin - allows operation only if user has admin role
                    return evaluateRoleCondition(conditionValue, context);
                case "regex":
                    // Example: regex:product_name_pattern
                    return evaluateRegexCondition(conditionValue, context);
                default:
                    // Unknown condition type, default to true to not block requests
                    return true;
            }
        }

        // Add more condition types as needed

        return true; // Default to true if condition doesn't match any known patterns
    }

    private boolean evaluateQuantityCondition(String conditionValue, RequestContext context, String operator) {
        // Extract quantity from the resource object if it's a Product
        if (context.getResourceObject() != null &&
            (context.getResourceObject().getClass().getSimpleName().equals("Product") ||
             context.getResourceObject().getClass().getSimpleName().equals("ExportSlip") ||
             context.getResourceObject().getClass().getSimpleName().equals("ImportSlip"))) {
            try {
                // Using reflection to get quantity - in real implementation, you'd have better type safety
                java.lang.reflect.Method getQuantity = context.getResourceObject().getClass().getMethod("getQuantity");
                Integer quantity = (Integer) getQuantity.invoke(context.getResourceObject());
                Integer threshold = Integer.parseInt(conditionValue);

                switch (operator) {
                    case "greater":
                        return quantity > threshold;
                    case "greater_equal":
                        return quantity >= threshold;
                    case "less":
                        return quantity < threshold;
                    case "less_equal":
                        return quantity <= threshold;
                    case "equal":
                        return quantity.equals(threshold);
                    case "not_equal":
                        return !quantity.equals(threshold);
                    default:
                        return true; // Default case
                }
            } catch (Exception e) {
                // If reflection fails, log and return true to not block requests
                return true;
            }
        }

        return true; // If object doesn't have quantity, condition passes
    }

    private boolean evaluateQuantityCondition(String conditionValue, RequestContext context) {
        // Default to less than operator if no operator specified
        return evaluateQuantityCondition(conditionValue, context, "less");
    }

    private boolean evaluateRoleCondition(String requiredRole, RequestContext context) {
        if (context.getUser() != null) {
            return context.getUser().getRole().equalsIgnoreCase(requiredRole);
        }
        return false;
    }

    private boolean evaluateRegexCondition(String pattern, RequestContext context) {
        if (context.getResourceObject() != null) {
            if (context.getResourceObject().getClass().getSimpleName().equals("Product")) {
                try {
                    java.lang.reflect.Method getName = context.getResourceObject().getClass().getMethod("getName");
                    String name = (String) getName.invoke(context.getResourceObject());
                    return Pattern.matches(pattern, name);
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }
}