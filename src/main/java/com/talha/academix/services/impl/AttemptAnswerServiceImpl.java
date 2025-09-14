package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.AttemptAnswerDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.UnmatchedExamException;
import com.talha.academix.model.Attempt;
import com.talha.academix.model.AttemptAnswer;
import com.talha.academix.model.Question;
import com.talha.academix.model.QuestionOption;
import com.talha.academix.repository.AttemptAnswerRepo;
import com.talha.academix.repository.AttemptRepo;
import com.talha.academix.repository.OptionRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.AttemptAnswerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptAnswerServiceImpl implements AttemptAnswerService {

    private final AttemptAnswerRepo attemptAnswerRepo;
    private final AttemptRepo attemptRepo;
    private final QuestionRepo questionRepo;
    private final OptionRepo optionRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AttemptAnswerDTO submitAnswer(Long attemptId, Long questionId, Long selectedOptionId) {
        Attempt attempt = attemptRepo.findById(attemptId)
            .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
    
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
    
        if (!question.getExam().getId().equals(attempt.getExam().getId())) {
            throw new UnmatchedExamException("This question does not belong to the exam for this attempt.");
        }
    
        QuestionOption questionOption = optionRepo.findById(selectedOptionId)
            .orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + selectedOptionId));
    
        if (!questionOption.getQuestion().getId().equals(questionId)) {
            throw new UnmatchedExamException("This option does not belong to the provided question.");
        }
    
        AttemptAnswer answer = new AttemptAnswer();
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedOption(questionOption);
        answer = attemptAnswerRepo.save(answer);
    
        return modelMapper.map(answer, AttemptAnswerDTO.class);
    }
    

    @Override
    public List<AttemptAnswerDTO> getAnswersByAttempt(Long attemptId) {
        return attemptAnswerRepo.findByAttemptId(attemptId)
            .stream()
            .map(a -> modelMapper.map(a, AttemptAnswerDTO.class))
            .collect(Collectors.toList());
    }


}