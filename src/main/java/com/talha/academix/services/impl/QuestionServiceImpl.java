package com.talha.academix.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.QuestionDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Exam;
import com.talha.academix.model.Question;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.QuestionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepo questionRepo;
    private final ExamRepo examRepo;
    private final CourseService courseService; // made final so injected
    private final ModelMapper mapper;

    @Override
    public QuestionDTO addQuestion(Long userid, Long examId, QuestionDTO dto) {
    
        Exam exam = examRepo.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

            if(courseService.teacherOwnership(userid, exam.getCourse().getCourseid())){
        Question question = mapper.map(dto, Question.class);
        question.setExam(exam);
        question = questionRepo.save(question);

        return mapper.map(question, QuestionDTO.class);
            }
            else throw new RoleMismatchException("Only teacher can add questions");
    }

    @Override
    public List<QuestionDTO> getQuestionsByExam(Long examId) {
        return questionRepo.findByExamId(examId)
            .stream()
            .map(q -> mapper.map(q, QuestionDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public QuestionDTO updateQuestion(Long userid, Long questionId, QuestionDTO dto) {
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        Long examId = question.getExam().getId();
        Exam exam = examRepo.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (courseService.teacherOwnership(userid, question.getExam().getCourse().getCourseid())) {

            mapper.getConfiguration().setSkipNullEnabled(true);
            mapper.map(dto, question);
            question.setId(questionId); // Ensure ID is set for update
            question.setExam(exam); // Reassign exam to ensure it's not null
            question = questionRepo.save(question);

            return mapper.map(question, QuestionDTO.class);
        } else {
            throw new RoleMismatchException("Only teacher can update question");
        }
    }

    @Override
    public void deleteQuestion(Long userid, Long questionId) {
        Question question = questionRepo.findById(questionId)
            .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (courseService.teacherOwnership(userid, question.getExam().getCourse().getCourseid())) {
            questionRepo.delete(question);
        } else {
            throw new RoleMismatchException("Only teacher can delete question");
        }
    }
}
