package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.ExportSlip;
import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.pep.PolicyEnforcementPoint;
import com.example.warehouse_management.pep.annotation.EnforcePolicy;
import com.example.warehouse_management.repository.ExportSlipRepository;
import com.example.warehouse_management.repository.ProductRepository;
import com.example.warehouse_management.service.ExportSlipService;
import com.example.warehouse_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/export")
public class ApiExportSlipController {

    @Autowired
    private ExportSlipService exportSlipService;

    @Autowired
    private ExportSlipRepository exportSlipRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PolicyEnforcementPoint pep;

    @GetMapping
    public ResponseEntity<List<ExportSlip>> getAllExportSlips() {
        // Check policy for reading export slips - this will force JWT authentication check too
        if (!pep.checkPermission("exportslip", "read")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(exportSlipService.getAllExportSlips());
    }

    @PostMapping
    @EnforcePolicy(resource = "exportslip", action = "create")
    public ExportSlip createExportSlip(ExportSlip exportSlip) {
        // Load product thực tế
        Product product = productRepository.findById(exportSlip.getProduct().getId())
            .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (product.getQuantity() == null) {
            throw new IllegalStateException("Product quantity is null");
        }

        int updatedQty = product.getQuantity() - exportSlip.getQuantity(); // export
        product.setQuantity(updatedQty);
        productRepository.save(product);

        exportSlip.setProduct(product); // gán lại product đầy đủ
        return exportSlipRepository.save(exportSlip);
    }

}