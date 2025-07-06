package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.AttemptAnswerDTO;

public interface AttemptAnswerService {
    AttemptAnswerDTO addAnswer(Long attemptId, AttemptAnswerDTO dto);
    List<AttemptAnswerDTO> getAnswersByAttempt(Long attemptId);
    void deleteAnswer(Long answerId);
}