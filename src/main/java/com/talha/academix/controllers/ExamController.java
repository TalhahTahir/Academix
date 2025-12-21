package com.talha.academix.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ExamController {

    private final ExamService examService;

    // Create exam under a course (teacher step 1)
    @PostMapping("/courses/{courseId}/exams/teachers/{teacherId}")
    public ExamResponse createExam(@PathVariable Long teacherId,
                                   @PathVariable Long courseId,
                                   @RequestBody CreateExamRequest req) {
        // enforce courseId from path (ignore body mismatch)
        req.setCourseId(courseId);
        return examService.createExam(teacherId, req);
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
    @PutMapping("/exams/{examId}/teachers/{teacherId}")
    public ExamResponse updateExam(@PathVariable Long teacherId,
                                   @PathVariable Long examId,
                                   @RequestBody CreateExamRequest req) {
        // IMPORTANT: courseId is not allowed to change; service ignores it.
        return examService.updateExam(teacherId, examId, req);
    }

    // Delete exam (teacher)
    @DeleteMapping("/exams/{examId}/teachers/{teacherId}")
    public void deleteExam(@PathVariable Long teacherId, @PathVariable Long examId) {
        examService.deleteExam(teacherId, examId);
    }
}