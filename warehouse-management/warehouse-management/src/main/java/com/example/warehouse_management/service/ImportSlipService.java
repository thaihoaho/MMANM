package com.example.warehouse_management.service;

import com.example.warehouse_management.entity.ImportSlip;
import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.repository.ImportSlipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImportSlipService {
    
    @Autowired
    private ImportSlipRepository importSlipRepository;
    
    @Autowired
    private ProductService productService;
    
    public List<ImportSlip> getAllImportSlips() {
        return importSlipRepository.findAll();
    }
    
    @Transactional
    public ImportSlip createImportSlip(ImportSlip importSlip) {
        // Get product
        Product product = importSlip.getProduct();
        
        // Update product quantity
        Integer currentQuantity = product.getQuantity();
        product.setQuantity(currentQuantity + importSlip.getQuantity());
        productService.saveProduct(product);
        
        // Save import slip
        return importSlipRepository.save(importSlip);
    }
    
    public ImportSlip getImportSlipById(Long id) {
        return importSlipRepository.findById(id).orElse(null);
    }
}