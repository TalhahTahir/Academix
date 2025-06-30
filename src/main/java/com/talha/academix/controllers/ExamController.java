package com.talha.academix.controllers;

import com.talha.academix.dto.ExamDTO;
import com.talha.academix.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    public ExamDTO addExam(@RequestBody ExamDTO dto) {
        return examService.addExam(dto);
    }

    @PutMapping("/{examId}")
    public ExamDTO updateExam(@PathVariable Long examId, @RequestBody ExamDTO dto) {
        return examService.updateExam(examId, dto);
    }

    @GetMapping("/{examId}")
    public ExamDTO getExamById(@PathVariable Long examId) {
        return examService.getExamById(examId);
    }

    @GetMapping("/course/{courseId}")
    public List<ExamDTO> getExamsByCourse(@PathVariable Long courseId) {
        return examService.getExamsByCourse(courseId);
    }

    @DeleteMapping("/{examId}")
    public void deleteExam(@PathVariable Long examId) {
        examService.deleteExam(examId);
    }
}
