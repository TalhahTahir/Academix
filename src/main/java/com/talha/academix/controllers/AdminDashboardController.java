package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.AdminDashboardDTO;
import com.talha.academix.services.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    
    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardDTO> getDashboard() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboard());
    }
    
}
