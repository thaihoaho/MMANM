package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.ApiResponse;
import com.warehouse.warehousemanager.dto.ImportDto;
import com.warehouse.warehousemanager.entity.Import;
import com.warehouse.warehousemanager.security.policy.PolicyEnforcementPoint;
import com.warehouse.warehousemanager.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/imports")
@CrossOrigin(origins = "*")
public class ImportController {

    @Autowired
    private ImportService importService;

    @Autowired
    private PolicyEnforcementPoint policyEnforcementPoint;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ImportDto>>> getAllImports(HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("imports", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<Import> imports = importService.findAll();
        List<ImportDto> importDtos = imports.stream()
            .map(importRecord -> new ImportDto(importRecord.getId(), importRecord.getProductId(), importRecord.getQuantity(), importRecord.getCreatedAt()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Import records retrieved successfully", importDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ImportDto>> getImportById(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("imports", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Import importRecord = importService.findById(id)
                .orElseThrow(() -> new RuntimeException("Import record not found with id: " + id));
        
        ImportDto importDto = new ImportDto(importRecord.getId(), importRecord.getProductId(), importRecord.getQuantity(), importRecord.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Import record retrieved successfully", importDto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ImportDto>> createImport(@RequestBody Import importRecord, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("imports", "create", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Import savedImport = importService.save(importRecord);
        ImportDto importDto = new ImportDto(savedImport.getId(), savedImport.getProductId(), savedImport.getQuantity(), savedImport.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Import record created successfully", importDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ImportDto>> updateImport(@PathVariable Long id, @RequestBody Import importDetails, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("imports", "update", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Import updatedImport = importService.update(id, importDetails);
        ImportDto importDto = new ImportDto(updatedImport.getId(), updatedImport.getProductId(), updatedImport.getQuantity(), updatedImport.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Import record updated successfully", importDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteImport(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("imports", "delete", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        importService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Import record deleted successfully"));
    }
}