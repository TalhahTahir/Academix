package com.talha.academix.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@examSecurity.isExamOwner(principal, #examId)")
    @PostMapping("/exams/{examId}/questions")
    public QuestionDTO addQuestion(@PathVariable Long examId,
                                   @RequestBody QuestionDTO dto) {
        dto.setExamId(examId);
        return questionService.addQuestion(examId, dto);
    }

    // List questions of an exam
    @GetMapping("/exams/{examId}/questions")
    public List<QuestionDTO> getQuestionsByExam(@PathVariable Long examId) {
        return questionService.getQuestionsByExam(examId);
    }

    // Update question (teacher)
    @PreAuthorize("@questionSecurity.isQuestionOwner(principal, #questionId)")
    @PutMapping("/questions/{questionId}")
    public QuestionDTO updateQuestion(@PathVariable Long questionId,
                                      @RequestBody QuestionDTO dto) {
        return questionService.updateQuestion(questionId, dto);
    }

    // Delete question (teacher)
    @PreAuthorize("@questionSecurity.isQuestionOwner(principal, #questionId)")
    @DeleteMapping("/questions/{questionId}")
    public void deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
    }
}