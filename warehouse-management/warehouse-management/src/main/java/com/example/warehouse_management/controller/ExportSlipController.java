package com.example.warehouse_management.controller;

import com.example.warehouse_management.entity.ExportSlip;
import com.example.warehouse_management.service.ExportSlipService;
import com.example.warehouse_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouse/export")
public class ExportSlipController {
    
    @Autowired
    private ExportSlipService exportSlipService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping("")
    public String listExportSlips(Model model) {
        model.addAttribute("exportSlips", exportSlipService.getAllExportSlips());
        return "warehouse/export-list";
    }
    
    @GetMapping("/create")
    public String showExportForm(Model model) {
        model.addAttribute("exportSlip", new ExportSlip());
        model.addAttribute("products", productService.getAllProducts());
        return "warehouse/export-form";
    }
    
    @PostMapping("/create")
    public String createExportSlip(@ModelAttribute ExportSlip exportSlip, RedirectAttributes redirectAttributes) {
        try {
            exportSlipService.createExportSlip(exportSlip);
            redirectAttributes.addFlashAttribute("success", "Product exported successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Error exporting product: " + e.getMessage());
        }
        return "redirect:/warehouse/export";
    }
}