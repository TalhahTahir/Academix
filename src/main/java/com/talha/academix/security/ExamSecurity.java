package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.ExamRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("examSecurity")
public class ExamSecurity {
    
    private final ExamRepo examRepo;

    public boolean isExamOwner(CustomUserDetails principal, Long examId) {
        if (principal == null || examId == null) {
            return false;
        }

        Exam exam = examRepo.findById(examId).orElseThrow(() -> new ResourceNotFoundException("Exam not fount with id: " + examId));

        return exam.getCourse().getTeacher().getUserid().equals(principal.getId());
    }
}
