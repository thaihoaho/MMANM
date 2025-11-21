package com.example.warehouse_management.service;

import com.example.warehouse_management.dto.ProductStatisticsDTO;
import com.example.warehouse_management.entity.Product;
import com.example.warehouse_management.repository.ImportSlipRepository;
import com.example.warehouse_management.repository.ExportSlipRepository;
import com.example.warehouse_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

@Service
public class StatisticsService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ImportSlipRepository importSlipRepository;
    
    @Autowired
    private ExportSlipRepository exportSlipRepository;
    
    public List<ProductStatisticsDTO> getProductStatistics(String searchTerm) {
        List<Product> products;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            // Search by product name
            products = productRepository.findByNameContainingIgnoreCase(searchTerm);
        } else {
            products = productRepository.findAll(Sort.by("name"));
        }
        
        List<ProductStatisticsDTO> statistics = new ArrayList<>();
        
        for (Product product : products) {
            ProductStatisticsDTO stat = new ProductStatisticsDTO();
            stat.setProductId(product.getId());
            stat.setProductName(product.getName());
            stat.setCurrentStock(product.getQuantity());
            
            // Get last import
            importSlipRepository.findTopByProductOrderByImportDateDesc(product).ifPresent(importSlip -> {
                stat.setLastImportDate(importSlip.getImportDate());
                stat.setLastImportQuantity(importSlip.getQuantity());
            });
            
            // Get last export
            exportSlipRepository.findTopByProductOrderByExportDateDesc(product).ifPresent(exportSlip -> {
                stat.setLastExportDate(exportSlip.getExportDate());
                stat.setLastExportQuantity(exportSlip.getQuantity());
            });
            
            // Get totals
            stat.setTotalImports(importSlipRepository.sumQuantityByProduct(product.getId()));
            stat.setTotalExports(exportSlipRepository.sumQuantityByProduct(product.getId()));
            
            statistics.add(stat);
        }
        
        return statistics;
    }
}