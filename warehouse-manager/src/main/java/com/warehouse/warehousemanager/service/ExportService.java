package com.warehouse.warehousemanager.service;

import com.warehouse.warehousemanager.entity.Export;
import com.warehouse.warehousemanager.entity.Product;
import com.warehouse.warehousemanager.repository.ExportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExportService {

    @Autowired
    private ExportRepository exportRepository;

    @Autowired
    private ProductService productService;

    public List<Export> findAll() {
        return exportRepository.findAll();
    }

    public Optional<Export> findById(Long id) {
        return exportRepository.findById(id);
    }

    public Export save(Export exportRecord) {
        // Check if there's enough quantity to export
        Product product = productService.findById(exportRecord.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < exportRecord.getQuantity()) {
            throw new RuntimeException("Not enough quantity in stock to export. Available: " +
                product.getQuantity() + ", Requested: " + exportRecord.getQuantity());
        }

        Export savedExport = exportRepository.save(exportRecord);

        // Update product quantity
        product.setQuantity(product.getQuantity() - exportRecord.getQuantity());
        productService.save(product);

        return savedExport;
    }

    public Export update(Long id, Export exportDetails) {
        Export existingExport = exportRepository.findById(id).orElseThrow(() -> new RuntimeException("Export record not found"));

        // Calculate the difference to adjust product quantity
        int quantityDifference = exportDetails.getQuantity() - existingExport.getQuantity();

        // Check if there's enough quantity to export (after adjustment)
        Product product = productService.findById(exportDetails.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() - quantityDifference < 0) {
            throw new RuntimeException("Not enough quantity in stock to export. Available: " +
                product.getQuantity() + ", Additional to Export: " + quantityDifference);
        }

        Export updatedExport = exportRepository.save(exportDetails);

        // Update product quantity based on the difference
        product.setQuantity(product.getQuantity() - quantityDifference);
        productService.save(product);

        return updatedExport;
    }

    public void deleteById(Long id) {
        Export existingExport = exportRepository.findById(id).orElseThrow(() -> new RuntimeException("Export record not found"));

        // Adjust product quantity before deletion
        Product product = productService.findById(existingExport.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setQuantity(product.getQuantity() + existingExport.getQuantity());
        productService.save(product);

        exportRepository.deleteById(id);
    }
}