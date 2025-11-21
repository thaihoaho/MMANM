package com.example.warehouse_management.service;

import com.example.warehouse_management.pdp.model.Effect;
import com.example.warehouse_management.pdp.model.Policy;
import com.example.warehouse_management.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Service
public class PolicyInitializationService {

    @Autowired
    private PolicyRepository policyRepository;

    @PostConstruct
    public void initializeDefaultPolicies() {
        // Check if policies already exist to avoid duplicates on restart
        if (policyRepository.findAll().size() > 0) {
            return; // Policies already exist
        }

        // Create default policies for warehouse operations
        createDefaultPolicies();
    }

    private void createDefaultPolicies() {
        // Policy 1: Admin can perform all operations on products
        Policy adminProductPolicy = new Policy(
            "admin-product-full-access",
            "Administrators can perform all operations on products",
            "product",
            "any",
            Effect.PERMIT,
            Arrays.asList("ADMIN"),
            Arrays.asList()
        );

        // Policy 2: Regular users can create and read products but not delete
        Policy userProductPolicy = new Policy(
            "user-product-access",
            "Regular users can create and read products",
            "product",
            "create",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        Policy userProductReadPolicy = new Policy(
            "user-product-read-access",
            "Regular users can read products",
            "product",
            "read",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 3: Only admins can delete products
        Policy adminDeletePolicy = new Policy(
            "admin-delete-products",
            "Only administrators can delete products",
            "product",
            "delete",
            Effect.PERMIT,
            Arrays.asList("ADMIN"),
            Arrays.asList()
        );

        // Policy 4: Admins can update products, users can update if quantity is below threshold
        Policy productUpdatePolicy = new Policy(
            "user-product-update-access",
            "Regular users can update products",
            "product",
            "update",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 5: Admins and users can create import slips
        Policy importSlipPolicy = new Policy(
            "import-slip-access",
            "Users can create import slips",
            "importslip",
            "create",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 6: Admins and users can create export slips
        Policy exportSlipPolicy = new Policy(
            "export-slip-access",
            "Users can create export slips",
            "exportslip",
            "create",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 7: Admins and users can read import slips
        Policy readImportSlipPolicy = new Policy(
            "read-import-slip-access",
            "Users can read import slips",
            "importslip",
            "read",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 8: Admins and users can read export slips
        Policy readExportSlipPolicy = new Policy(
            "read-export-slip-access",
            "Users can read export slips",
            "exportslip",
            "read",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList()
        );

        // Policy 9: Advanced policy - users can only export products with sufficient quantity
        Policy exportWithSufficientQuantity = new Policy(
            "export-sufficient-quantity",
            "Users can only export products with sufficient quantity in stock",
            "exportslip",
            "create",
            Effect.PERMIT,
            Arrays.asList("USER", "ADMIN"),
            Arrays.asList("quantity>=:1")  // The product must have at least 1 in stock for export
        );

        // Policy 10: Product name pattern policy - only admin can modify products with certain names
        Policy productNamePatternPolicy = new Policy(
            "product-name-pattern-policy",
            "Special policy for products with names matching a pattern",
            "product",
            "update",
            Effect.PERMIT,
            Arrays.asList("ADMIN"),
            Arrays.asList("regex:^special_.*$")  // Only affects products with names starting with "special_"
        );

        // Save all policies
        List<Policy> policies = Arrays.asList(
            adminProductPolicy, userProductPolicy, userProductReadPolicy, 
            adminDeletePolicy, productUpdatePolicy, importSlipPolicy, 
            exportSlipPolicy, readImportSlipPolicy, readExportSlipPolicy,
            exportWithSufficientQuantity, productNamePatternPolicy
        );

        for (Policy policy : policies) {
            // Check if a policy with the same name already exists
            if (!policyRepository.findAll().stream().anyMatch(p -> p.getName().equals(policy.getName()))) {
                policyRepository.save(policy);
            }
        }
    }
}