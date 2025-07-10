package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.QuestionDTO;

public interface QuestionService {
    QuestionDTO addQuestion(Long userid, Long examId, QuestionDTO dto);
    List<QuestionDTO> getQuestionsByExam(Long examId);
    QuestionDTO updateQuestion(Long userid, Long questionId, QuestionDTO dto);
    void deleteQuestion(Long userid, Long questionId);
}