package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.ImportSlip;
import com.example.warehouse_management.pep.PolicyEnforcementPoint;
import com.example.warehouse_management.pep.annotation.EnforcePolicy;
import com.example.warehouse_management.service.ImportSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse/import")
public class ApiImportSlipController {

    @Autowired
    private ImportSlipService importSlipService;

    @Autowired
    private PolicyEnforcementPoint pep;

    @GetMapping
    public ResponseEntity<List<ImportSlip>> getAllImportSlips() {
        // Check policy for reading import slips - this will force JWT authentication check too
        if (!pep.checkPermission("importslip", "read")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(importSlipService.getAllImportSlips());
    }

    @PostMapping
    @EnforcePolicy(resource = "importslip", action = "create")
    public ResponseEntity<ImportSlip> createImportSlip(@RequestBody ImportSlip importSlip) {
        ImportSlip createdSlip = importSlipService.createImportSlip(importSlip);
        return ResponseEntity.ok(createdSlip);
    }
}