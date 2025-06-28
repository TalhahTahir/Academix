package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.EnrollmentDTO;

public interface EnrollmentServices {
        EnrollmentDTO enrollStudent(EnrollmentDTO dto);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId); 
    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);
    void deleteEnrollment(Long id);
}
