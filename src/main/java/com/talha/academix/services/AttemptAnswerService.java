package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.AttemptAnswerDTO;

public interface AttemptAnswerService {
    List<AttemptAnswerDTO> getAnswersByAttempt(Long attemptId);
    public AttemptAnswerDTO submitAnswer(Long attemptId, Long questionId, Long selectedOptionId);
}