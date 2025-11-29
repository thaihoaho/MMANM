package com.warehouse.warehousemanager.controller;

import com.warehouse.warehousemanager.dto.ApiResponse;
import com.warehouse.warehousemanager.dto.ExportDto;
import com.warehouse.warehousemanager.entity.Export;
import com.warehouse.warehousemanager.security.policy.PolicyEnforcementPoint;
import com.warehouse.warehousemanager.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exports")
@CrossOrigin(origins = "*")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private PolicyEnforcementPoint policyEnforcementPoint;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExportDto>>> getAllExports(HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("exports", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        List<Export> exports = exportService.findAll();
        List<ExportDto> exportDtos = exports.stream()
            .map(exportRecord -> new ExportDto(exportRecord.getId(), exportRecord.getProductId(), exportRecord.getQuantity(), exportRecord.getCreatedAt()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("Export records retrieved successfully", exportDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExportDto>> getExportById(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("exports", "read", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Export exportRecord = exportService.findById(id)
                .orElseThrow(() -> new RuntimeException("Export record not found with id: " + id));
        
        ExportDto exportDto = new ExportDto(exportRecord.getId(), exportRecord.getProductId(), exportRecord.getQuantity(), exportRecord.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Export record retrieved successfully", exportDto));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExportDto>> createExport(@RequestBody Export exportRecord, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("exports", "create", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Export savedExport = exportService.save(exportRecord);
        ExportDto exportDto = new ExportDto(savedExport.getId(), savedExport.getProductId(), savedExport.getQuantity(), savedExport.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Export record created successfully", exportDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExportDto>> updateExport(@PathVariable Long id, @RequestBody Export exportDetails, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("exports", "update", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        Export updatedExport = exportService.update(id, exportDetails);
        ExportDto exportDto = new ExportDto(updatedExport.getId(), updatedExport.getProductId(), updatedExport.getQuantity(), updatedExport.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success("Export record updated successfully", exportDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExport(@PathVariable Long id, HttpServletRequest request) {
        if (!policyEnforcementPoint.checkAccess("exports", "delete", request)) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
        }

        exportService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Export record deleted successfully"));
    }
}