package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Question;
import com.talha.academix.repository.QuestionRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("questionSecurity")
public class QuestionSecurity {
    
    private final QuestionRepo questionRepo;

    public boolean isQuestionOwner(CustomUserDetails principal, Long questionId) {
        if (principal == null || questionId == null) {
            return false;
        }

        Question question = questionRepo.findById(questionId).orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));
        return question.getExam().getCourse().getTeacher().getUserid().equals(principal.getId());
    }
}
