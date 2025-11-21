package com.example.warehouse_management.repository;

import com.example.warehouse_management.entity.ExportSlip;
import com.example.warehouse_management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExportSlipRepository extends JpaRepository<ExportSlip, Long> {
    Optional<ExportSlip> findTopByProductOrderByExportDateDesc(Product product);
    
    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM ExportSlip e WHERE e.product.id = ?1")
    Integer sumQuantityByProduct(Long productId);
}