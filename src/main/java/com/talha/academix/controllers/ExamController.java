package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.ExamDTO;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ExamDTO createExam(@PathVariable Long teacherId, @RequestBody ExamDTO dto) {
        return examService.createExam(teacherId, dto);
    }

    @PostMapping("update/{teacherId}/{examId}")
    public ExamDTO updateExam(@PathVariable Long teacherId, @PathVariable Long examId, @RequestBody ExamDTO dto) {
        return examService.updateExam(teacherId, examId, dto);
    }

    @GetMapping("{examId}")
    public ExamDTO getExamById(@PathVariable Long examId) {
        return examService.getExamById(examId);
    }
    
    @GetMapping("{courseId}")
    public List<ExamDTO> getExamByCourseId(@PathVariable Long courseId) {
        return examService.getExamsByCourse(courseId);
    }
    
    @DeleteMapping("{teacherId}/{examId}")
    public void deleteExam(@PathVariable Long teacherId, @PathVariable Long examId) {
        examService.deleteExam(teacherId, examId);
    }

    @PostMapping("check")
    public Double postMethodName(@RequestBody AttemptDTO attemptDTO) {
        return examService.checkExam(attemptDTO);
    }
    
}
