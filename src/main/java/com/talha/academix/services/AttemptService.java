package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.AttemptDTO;

public interface AttemptService {
    AttemptDTO startAttempt(Long examId, Long studentId);
    AttemptDTO submitAttempt(Long attemptId, AttemptDTO dto);
    AttemptDTO getAttemptById(Long attemptId);
    List<AttemptDTO> getAttemptsByStudent(Long studentId);
    List<AttemptDTO> getAttemptsByExam(Long examId);
}