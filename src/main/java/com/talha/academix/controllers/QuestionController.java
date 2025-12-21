package com.talha.academix.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.QuestionDTO;
import com.talha.academix.services.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionController {

    private final QuestionService questionService;

    // Add question to an exam (teacher step 2)
    @PostMapping("/exams/{examId}/questions/teachers/{teacherId}")
    public QuestionDTO addQuestion(@PathVariable Long teacherId,
                                   @PathVariable Long examId,
                                   @RequestBody QuestionDTO dto) {
        dto.setExamId(examId);
        return questionService.addQuestion(teacherId, examId, dto);
    }

    // List questions of an exam
    @GetMapping("/exams/{examId}/questions")
    public List<QuestionDTO> getQuestionsByExam(@PathVariable Long examId) {
        return questionService.getQuestionsByExam(examId);
    }

    // Update question (teacher)
    @PutMapping("/questions/{questionId}/teachers/{teacherId}")
    public QuestionDTO updateQuestion(@PathVariable Long teacherId,
                                      @PathVariable Long questionId,
                                      @RequestBody QuestionDTO dto) {
        return questionService.updateQuestion(teacherId, questionId, dto);
    }

    // Delete question (teacher)
    @DeleteMapping("/questions/{questionId}/teachers/{teacherId}")
    public void deleteQuestion(@PathVariable Long teacherId,
                               @PathVariable Long questionId) {
        questionService.deleteQuestion(teacherId, questionId);
    }
}