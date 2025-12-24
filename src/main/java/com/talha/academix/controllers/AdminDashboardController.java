package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.services.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashBoardDTO> getDashboard() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboard());
    }
    
}
