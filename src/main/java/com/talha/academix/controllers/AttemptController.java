package com.talha.academix.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.services.AttemptService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttemptController {

    private final AttemptService attemptService;

    @PreAuthorize("@enrollmentSecurity.isEnrolled(principal, #examId)")
    // Student: start an attempt for an exam
    @PostMapping("/exams/{examId}/attempts/students/{studentId}")
    public AttemptDTO startAttempt(@PathVariable Long examId,
                                   @PathVariable Long studentId) {
        return attemptService.startAttempt(examId, studentId);
    }

    // Student: submit attempt (finalize + grade)
    @PreAuthorize("@attemptSecurity.isAttemptOwner(principal, #attemptId)")
    @PostMapping("/attempts/{attemptId}/submit/students/{studentId}")
    public AttemptDTO submitAttempt(@PathVariable Long attemptId,
                                    @PathVariable Long studentId,
                                    @Valid @RequestBody AttemptDTO dto) {
        dto.setStudentId(studentId);
        return attemptService.submitAttempt(attemptId, dto);
    }

    @PreAuthorize("@attemptSecurity.isAttemptOwner(principal, #attemptId) or hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/attempts/{attemptId}")
    public AttemptDTO getAttemptById(@PathVariable Long attemptId) {
        return attemptService.getAttemptById(attemptId);
    }

    // Teacher/admin: list attempts for an exam
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/exams/{examId}/attempts")
    public List<AttemptDTO> getAttemptsByExam(@PathVariable Long examId) {
        return attemptService.getAttemptsByExam(examId);
    }

    // Student: list attempts for a student
    @PreAuthorize("hasRole('ROLE_ADMIN') or (principal != null and principal.id == #studentId)")
    @GetMapping("/students/{studentId}/attempts")
    public List<AttemptDTO> getAttemptsByStudent(@PathVariable Long studentId) {
        return attemptService.getAttemptsByStudent(studentId);
    }
}