package com.talha.academix.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Attempt;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.AttemptRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.AttemptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepo attemptRepo;
    private final ExamRepo examRepo;
    private final ModelMapper modelMapper;

    @Override
    public AttemptDTO startAttempt(Long examId, Long studentId) {
        Exam exam = examRepo.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        Attempt attempt = new Attempt();
        attempt.setExam(exam);
        attempt.setStudentId(studentId);
        attempt.setStartedAt(LocalDateTime.now());
        attempt = attemptRepo.save(attempt);

        return modelMapper.map(attempt, AttemptDTO.class);
    }

    @Override
    public List<AttemptDTO> getAttemptsByStudent(Long studentId) {
        return attemptRepo.findByStudentId(studentId)
            .stream()
            .map(a -> modelMapper.map(a, AttemptDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public void completeAttempt(Long attemptId) {
        Attempt attempt = attemptRepo.findById(attemptId)
            .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        attempt.setCompletedAt(LocalDateTime.now());
        attemptRepo.save(attempt);
    }

    @Override
    public AttemptDTO submitAttempt(Long attemptId, AttemptDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submitAttempt'");
    }

    @Override
    public AttemptDTO getAttemptById(Long attemptId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttemptById'");
    }

    @Override
    public List<AttemptDTO> getAttemptsByExam(Long examId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttemptsByExam'");
    }
}
