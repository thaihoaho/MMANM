package com.example.warehouse_management.controller;

import com.example.warehouse_management.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping({"/dashboard", "/"})
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    @GetMapping("")
    public String showStatistics(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("statistics", statisticsService.getProductStatistics(search));
        model.addAttribute("search", search);
        return "statistics/dashboard";
    }
}