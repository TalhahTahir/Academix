package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.QuestionDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Exam;
import com.talha.academix.model.Question;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepo questionRepo;
    private final ExamRepo examRepo;
    private final ModelMapper modelMapper;

    @Override
    public QuestionDTO addQuestion(Long examId, QuestionDTO dto) {
        Exam exam = examRepo.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        Question question = modelMapper.map(dto, Question.class);
        question.setExam(exam);
        question = questionRepo.save(question);

        return modelMapper.map(question, QuestionDTO.class);
    }

    @Override
    public List<QuestionDTO> getQuestionsByExam(Long examId) {
        return questionRepo.findByExamId(examId)
            .stream()
            .map(q -> modelMapper.map(q, QuestionDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public QuestionDTO updateQuestion(Long questionId, QuestionDTO dto) {
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        
        question.setText(dto.getText());
        question = questionRepo.save(question);

        return modelMapper.map(question, QuestionDTO.class);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        questionRepo.delete(question);
    }
}
