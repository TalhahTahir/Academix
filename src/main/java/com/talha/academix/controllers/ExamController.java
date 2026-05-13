package com.talha.academix.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;
import com.talha.academix.services.ExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ExamController {

    private final ExamService examService;

    // Create exam under a course (teacher step 1)
    @PreAuthorize("@courseSecurity.isCourseOwner(principal, #courseId)")
    @PostMapping("/courses/{courseId}/exams/")
    public ExamResponse createExam(@PathVariable Long courseId,
                                   @Valid @RequestBody CreateExamRequest req) {
        // enforce courseId from path (ignore body mismatch)
        req.setCourseId(courseId);
        return examService.createExam(req);
    }

    // List exams of a course
    @GetMapping("/courses/{courseId}/exams")
    public List<ExamResponse> getExamsByCourse(@PathVariable Long courseId) {
        return examService.getExamsByCourse(courseId);
    }

    // Get exam by id
    @GetMapping("/exams/{examId}")
    public ExamResponse getExamById(@PathVariable Long examId) {
        return examService.getExamById(examId);
    }

    // Update exam title only (teacher)
    @PreAuthorize("@examSecurity.isExamOwner(principal, #examId)")
    @PutMapping("/exams/{examId}")
    public ExamResponse updateExam(@PathVariable Long examId,
                                   @Valid @RequestBody CreateExamRequest req) {
        // IMPORTANT: courseId is not allowed to change; service ignores it.
        return examService.updateExam(examId, req);
    }

    // Delete exam (teacher)
    @PreAuthorize("@examSecurity.isExamOwner(principal, #examId)")
    @DeleteMapping("/exams/{examId}")
    public void deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
    }
}