package com.warehouse.warehousemanager.repository;

import com.warehouse.warehousemanager.entity.Export;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportRepository extends JpaRepository<Export, Long> {
}