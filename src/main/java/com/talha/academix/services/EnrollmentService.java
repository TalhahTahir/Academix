package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.EnrollmentDTO;

public interface EnrollmentService {
    EnrollmentDTO enrollStudent(Long studentId, Long courseId);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId);
    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);
    void withdrawEnrollment(Long enrollmentId);
    EnrollmentDTO enrollmentValidation(Long courseid, Long userid);

    EnrollmentDTO updateEnrollment(EnrollmentDTO enrollment);

    public void finalizeEnrollment(Long userid, Long courseid);

}