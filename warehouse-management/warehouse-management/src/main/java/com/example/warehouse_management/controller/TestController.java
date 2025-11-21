package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.pep.PolicyEnforcementPoint;
import com.example.warehouse_management.pdp.PolicyDecisionPoint;
import com.example.warehouse_management.pdp.model.Decision;
import com.example.warehouse_management.pdp.model.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private PolicyEnforcementPoint pep;

    @Autowired
    private PolicyDecisionPoint pdp;

    // Test endpoint to check if policy enforcement is working
    @GetMapping("/access-check")
    public ResponseEntity<Map<String, Object>> testAccess(@RequestParam String resource, @RequestParam String action) {
        Map<String, Object> response = new HashMap<>();
        
        boolean hasAccess = pep.checkPermission(resource, action);
        
        response.put("resource", resource);
        response.put("action", action);
        response.put("hasAccess", hasAccess);
        response.put("message", hasAccess ? "Access granted by policy" : "Access denied by policy");
        
        return ResponseEntity.ok(response);
    }

    // Test endpoint with resource object
    @PostMapping("/access-check-with-object")
    public ResponseEntity<Map<String, Object>> testAccessWithObject(@RequestParam String resource,
                                                                   @RequestParam String action,
                                                                   @RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();

        boolean hasAccess = pep.checkPermission(resource, action, product);
        com.example.warehouse_management.pdp.model.Decision decision = pep.getDetailedDecision(resource, action, product);

        response.put("resource", resource);
        response.put("action", action);
        response.put("product", product);
        response.put("hasAccess", hasAccess);
        response.put("decisionDetails", decision);
        response.put("message", hasAccess ? "Access granted by policy" : "Access denied by policy");

        return ResponseEntity.ok(response);
    }

    // Direct PDP test
    @PostMapping("/pdp-test")
    public ResponseEntity<Decision> testPDP(@RequestParam String resource, 
                                           @RequestParam String action,
                                           @RequestBody(required = false) Product product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        com.example.warehouse_management.entity.User user = null;
        
        if (authentication != null && authentication.getPrincipal() instanceof com.example.warehouse_management.entity.User) {
            user = (com.example.warehouse_management.entity.User) authentication.getPrincipal();
        }
        
        RequestContext context = new RequestContext(user, resource, action, product);
        Decision decision = pdp.evaluate(context);
        
        return ResponseEntity.ok(decision);
    }
}