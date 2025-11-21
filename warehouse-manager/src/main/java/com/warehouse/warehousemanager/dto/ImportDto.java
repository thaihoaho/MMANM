package com.warehouse.warehousemanager.dto;

import java.time.LocalDateTime;

public class ImportDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;

    // Constructors
    public ImportDto() {}

    public ImportDto(Long id, Long productId, Integer quantity, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}