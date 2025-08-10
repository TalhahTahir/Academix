package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.model.Enrollment;

public interface EnrollmentService {
    EnrollmentDTO enrollStudent(Long studentId, Long courseId);

    EnrollmentDTO getEnrollmentById(Long enrollmentId);

    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);

    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);

    void withdrawEnrollment(Long enrollmentId);

    EnrollmentDTO enrollmentValidation(Long courseid, Long userid);

    EnrollmentDTO updateEnrollment(EnrollmentDTO enrollment);

    EnrollmentDTO finalizeEnrollment(Long studentId, Long courseId);

    Enrollment getEnrollmentEntity(Long enrollmentId); // returns entity (or throw)

    void updateCompletionPercentage(Long enrollmentId, double percentage);

}