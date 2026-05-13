package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.ExamRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("enrollmentSecurity")
public class EnrollmentSecurity {

    private final EnrollmentRepo enrollmentRepo;
    private final ExamRepo examRepo;

    public boolean isEnrolled(CustomUserDetails principal, Long ExamId) {
        if (principal == null || ExamId == null) {
            return false;
        }
        Exam exam = examRepo.findById(ExamId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + ExamId));

        return enrollmentRepo.existsByStudent_UseridAndCourse_CourseId(principal.getId(),
                exam.getCourse().getCourseId());
    }

    public boolean isEnrolledById(CustomUserDetails principal, Long enrollmentId) {
        if (principal == null || enrollmentId == null) {
            return false;
        }
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        return enrollment.getStudent().getUserid().equals(principal.getId());
    }
}
