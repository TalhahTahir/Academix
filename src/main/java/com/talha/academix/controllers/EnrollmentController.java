package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.services.EnrollmentServices;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentServices enrollmentService;

    @PostMapping
    public EnrollmentDTO enrollStudent(@RequestBody EnrollmentDTO dto) {
        return enrollmentService.enrollStudent(dto);
    }

    @GetMapping("/student/{studentId}")
    public List<EnrollmentDTO> getEnrollmentsByStudent(@PathVariable Long studentId) {
        return enrollmentService.getEnrollmentsByStudent(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<EnrollmentDTO> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return enrollmentService.getEnrollmentsByCourse(courseId);
    }

    @DeleteMapping("/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }

}