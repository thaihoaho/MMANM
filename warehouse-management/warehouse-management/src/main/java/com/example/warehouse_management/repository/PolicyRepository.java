package com.example.warehouse_management.repository;

import com.example.warehouse_management.pdp.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    @Query("SELECT p FROM Policy p WHERE p.resource = :resource AND p.action = :action")
    List<Policy> findByResourceAndAction(@Param("resource") String resource, @Param("action") String action);
}