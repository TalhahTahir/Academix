package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams/")
public class ExamController {

    private final ExamService examService;

    @PostMapping("create/{teacherId}")
    public ExamResponse createExam(@PathVariable Long teacherId, @RequestBody CreateExamRequest req) {
        return examService.createExam(teacherId, req);
    }

    @PutMapping("update/{teacherId}/{examId}")
    public ExamResponse updateExam(@PathVariable Long teacherId,
                                   @PathVariable Long examId,
                                   @RequestBody CreateExamRequest req) {
        return examService.updateExam(teacherId, examId, req);
    }

    @GetMapping("{examId}")
    public ExamResponse getExamById(@PathVariable Long examId) {
        return examService.getExamById(examId);
    }

    @GetMapping("courses/{courseId}")
    public List<ExamResponse> getExamByCourseId(@PathVariable Long courseId) {
        return examService.getExamsByCourse(courseId);
    }

    @DeleteMapping("{teacherId}/{examId}")
    public void deleteExam(@PathVariable Long teacherId, @PathVariable Long examId) {
        examService.deleteExam(teacherId, examId);
    }

}