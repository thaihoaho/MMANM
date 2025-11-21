package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.pep.PolicyEnforcementPoint;
import com.example.warehouse_management.pep.annotation.EnforcePolicy;
import com.example.warehouse_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ApiProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PolicyEnforcementPoint pep;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        // Check policy for reading products - this will force JWT authentication check too
        if (!pep.checkPermission("product", "read")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        // Check policy for reading specific product - this will force JWT authentication check too
        Product product = productService.getProductById(id).orElse(null);
        if (product != null && !pep.checkPermission("product", "read", product)) {
            return ResponseEntity.status(403).build();
        }
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @EnforcePolicy(resource = "product", action = "create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    @EnforcePolicy(resource = "product", action = "update")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        Product updatedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @EnforcePolicy(resource = "product", action = "delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // Check policy for deletion first
        Product product = productService.getProductById(id).orElse(null);
        if (product != null && !pep.checkPermission("product", "delete", product)) {
            return ResponseEntity.status(403).build();
        }
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}