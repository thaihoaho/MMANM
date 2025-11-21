package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.ImportSlip;
import com.example.warehouse_management.service.ImportSlipService;
import com.example.warehouse_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouse/import")
public class ImportSlipController {
    
    @Autowired
    private ImportSlipService importSlipService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("")
    public String listImportSlips(Model model) {
        model.addAttribute("importSlips", importSlipService.getAllImportSlips());
        return "warehouse/import-list";
    }
    
    @GetMapping("/create")
    public String showImportForm(Model model) {
        model.addAttribute("importSlip", new ImportSlip());
        model.addAttribute("products", productService.getAllProducts());
        return "warehouse/import-form";
    }
    
    @PostMapping("/create")
    public String createImportSlip(@ModelAttribute ImportSlip importSlip, RedirectAttributes redirectAttributes) {
        try {
            importSlipService.createImportSlip(importSlip);
            redirectAttributes.addFlashAttribute("success", "Product imported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error importing product: " + e.getMessage());
        }
        return "redirect:/warehouse/import";
    }
}