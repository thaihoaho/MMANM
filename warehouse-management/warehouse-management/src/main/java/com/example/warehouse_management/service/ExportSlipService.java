package com.example.warehouse_management.service;

import com.example.warehouse_management.entity.ExportSlip;
import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.repository.ExportSlipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExportSlipService {
    
    @Autowired
    private ExportSlipRepository exportSlipRepository;
    
    @Autowired
    private ProductService productService;
    
    public List<ExportSlip> getAllExportSlips() {
        return exportSlipRepository.findAll();
    }
    
    @Transactional
    public ExportSlip createExportSlip(ExportSlip exportSlip) throws IllegalStateException {
        // Get product
        Product product = exportSlip.getProduct();
        
        // Check if there's enough quantity
        if (product.getQuantity() < exportSlip.getQuantity()) {
            throw new IllegalStateException("Insufficient product quantity");
        }
        
        // Update product quantity
        product.setQuantity(product.getQuantity() - exportSlip.getQuantity());
        productService.saveProduct(product);
        
        // Save export slip
        return exportSlipRepository.save(exportSlip);
    }
    
    public ExportSlip getExportSlipById(Long id) {
        return exportSlipRepository.findById(id).orElse(null);
    }
}