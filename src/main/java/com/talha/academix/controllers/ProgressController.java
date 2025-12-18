package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.EnrollmentStatsDTO;
import com.talha.academix.services.ProgressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/enrollments/{enrollmentId}/stats")
    public ResponseEntity<EnrollmentStatsDTO> getEnrollmentStats(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(progressService.getEnrollmentStats(enrollmentId));
    }
}