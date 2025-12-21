package com.talha.academix.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.services.AttemptService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttemptController {

    private final AttemptService attemptService;

    // Student: start an attempt for an exam
    @PostMapping("/exams/{examId}/attempts/students/{studentId}")
    public AttemptDTO startAttempt(@PathVariable Long examId,
                                   @PathVariable Long studentId) {
        return attemptService.startAttempt(examId, studentId);
    }

    // Student: submit attempt (finalize + grade)
    @PostMapping("/attempts/{attemptId}/submit/students/{studentId}")
    public AttemptDTO submitAttempt(@PathVariable Long attemptId,
                                    @PathVariable Long studentId,
                                    @RequestBody AttemptDTO dto) {
        dto.setStudentId(studentId);
        return attemptService.submitAttempt(attemptId, dto);
    }

    @GetMapping("/attempts/{attemptId}")
    public AttemptDTO getAttemptById(@PathVariable Long attemptId) {
        return attemptService.getAttemptById(attemptId);
    }

    // Teacher/admin: list attempts for an exam
    @GetMapping("/exams/{examId}/attempts")
    public List<AttemptDTO> getAttemptsByExam(@PathVariable Long examId) {
        return attemptService.getAttemptsByExam(examId);
    }

    // Student: list attempts for a student
    @GetMapping("/students/{studentId}/attempts")
    public List<AttemptDTO> getAttemptsByStudent(@PathVariable Long studentId) {
        return attemptService.getAttemptsByStudent(studentId);
    }
}