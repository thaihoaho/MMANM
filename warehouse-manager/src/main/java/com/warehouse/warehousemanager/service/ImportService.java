package com.warehouse.warehousemanager.service;

import com.warehouse.warehousemanager.entity.Import;
import com.warehouse.warehousemanager.entity.Product;
import com.warehouse.warehousemanager.repository.ImportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ImportService {

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private ProductService productService;

    public List<Import> findAll() {
        return importRepository.findAll();
    }

    public Optional<Import> findById(Long id) {
        return importRepository.findById(id);
    }

    public Import save(Import importRecord) {
        Import savedImport = importRepository.save(importRecord);

        // Update product quantity
        Product product = productService.findById(importRecord.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + importRecord.getQuantity());
        productService.save(product);

        return savedImport;
    }

    public Import update(Long id, Import importDetails) {
        Import existingImport = importRepository.findById(id).orElseThrow(() -> new RuntimeException("Import record not found"));

        // Calculate the difference to adjust product quantity
        int quantityDifference = importDetails.getQuantity() - existingImport.getQuantity();

        Import updatedImport = importRepository.save(importDetails);

        // Update product quantity based on the difference
        Product product = productService.findById(importDetails.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + quantityDifference);
        productService.save(product);

        return updatedImport;
    }

    public void deleteById(Long id) {
        Import existingImport = importRepository.findById(id).orElseThrow(() -> new RuntimeException("Import record not found"));

        // Adjust product quantity before deletion
        Product product = productService.findById(existingImport.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() - existingImport.getQuantity());
        productService.save(product);

        importRepository.deleteById(id);
    }
}