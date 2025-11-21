package com.example.warehouse_management.dto;

import java.time.LocalDateTime;

public class ProductStatisticsDTO {
    private Long productId;
    private String productName;
    private Integer currentStock;
    private LocalDateTime lastImportDate;
    private Integer lastImportQuantity;
    private LocalDateTime lastExportDate;
    private Integer lastExportQuantity;
    private Integer totalImports;
    private Integer totalExports;
    
    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public LocalDateTime getLastImportDate() {
        return lastImportDate;
    }

    public void setLastImportDate(LocalDateTime lastImportDate) {
        this.lastImportDate = lastImportDate;
    }

    public Integer getLastImportQuantity() {
        return lastImportQuantity;
    }

    public void setLastImportQuantity(Integer lastImportQuantity) {
        this.lastImportQuantity = lastImportQuantity;
    }

    public LocalDateTime getLastExportDate() {
        return lastExportDate;
    }

    public void setLastExportDate(LocalDateTime lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    public Integer getLastExportQuantity() {
        return lastExportQuantity;
    }

    public void setLastExportQuantity(Integer lastExportQuantity) {
        this.lastExportQuantity = lastExportQuantity;
    }

    public Integer getTotalImports() {
        return totalImports;
    }

    public void setTotalImports(Integer totalImports) {
        this.totalImports = totalImports;
    }

    public Integer getTotalExports() {
        return totalExports;
    }

    public void setTotalExports(Integer totalExports) {
        this.totalExports = totalExports;
    }
}