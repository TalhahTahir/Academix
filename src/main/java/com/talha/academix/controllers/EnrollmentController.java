package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.services.EnrollmentService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/count")
    public Long countAllEnrollments() {
        return enrollmentService.countAllEnrollments();
    }

    @GetMapping("/count/course/{courseId}")
    public Long countEnrollmentsByCourse(@PathVariable Long courseId) {
        return enrollmentService.countEnrollmentsByCourse(courseId);
    }

    @GetMapping("/count/student/{studentId}")
    public Long countEnrollmentsByStudent(@PathVariable Long studentId) {
        return enrollmentService.countEnrollmentsByStudent(studentId);
    }

    @GetMapping("/student/all/{id}")
    public List<EnrollmentDTO> getEnrollmentByStudent(@PathVariable("id") Long studentId) {
        return enrollmentService.getEnrollmentsByStudent(studentId);
    }

    @GetMapping("/{id}")
    public EnrollmentDTO getEnrollmentById(@PathVariable("id") Long enrollmentId) {
        return enrollmentService.getEnrollmentById(enrollmentId);
    }

    @GetMapping("/course/all/{id}")
    public List<EnrollmentDTO> getEnrollmentsByCourse(@PathVariable("id") Long courseId) {
        return enrollmentService.getEnrollmentsByCourse(courseId);
    }

    @PreAuthorize("@enrollmentSecurity.isEnrolled(principal, #id)")
    @DeleteMapping("/{id}")
    public void withdrawEnrollment(@PathVariable Long id) {
        enrollmentService.withdrawEnrollment(id);
    }

}
