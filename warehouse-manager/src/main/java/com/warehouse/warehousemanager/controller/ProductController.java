package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.ApiResponse;
import com.warehouse.warehousemanager.dto.ProductDto;
import com.warehouse.warehousemanager.entity.Product;
import com.warehouse.warehousemanager.security.policy.PolicyEnforcementPoint;
import com.warehouse.warehousemanager.service.ProductService;
import com.warehouse.warehousemanager.service.TrustLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private PolicyEnforcementPoint policyEnforcementPoint;

    @Autowired
    private TrustLogService trustLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts(HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("products", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<Product> products = productService.findAll();
        List<ProductDto> productDtos = products.stream()
            .map(product -> new ProductDto(product.getId(), product.getName(), product.getQuantity(), product.getLocation()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("products", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Product product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        ProductDto productDto = new ProductDto(product.getId(), product.getName(), product.getQuantity(), product.getLocation());
        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", productDto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@RequestBody Product product, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("products", "create", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Product savedProduct = productService.save(product);
        ProductDto productDto = new ProductDto(savedProduct.getId(), savedProduct.getName(), savedProduct.getQuantity(), savedProduct.getLocation());
        return ResponseEntity.ok(ApiResponse.success("Product created successfully", productDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable Long id, @RequestBody Product productDetails, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("products", "update", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Product updatedProduct = productService.update(id, productDetails);
        ProductDto productDto = new ProductDto(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getQuantity(), updatedProduct.getLocation());
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("products", "delete", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        productService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
}