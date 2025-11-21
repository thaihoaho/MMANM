package com.example.warehouse_management.repository;

import com.example.warehouse_management.entity.ImportSlip;
import com.example.warehouse_management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ImportSlipRepository extends JpaRepository<ImportSlip, Long> {
    Optional<ImportSlip> findTopByProductOrderByImportDateDesc(Product product);
    
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM ImportSlip i WHERE i.product.id = ?1")
    Integer sumQuantityByProduct(Long productId);
}