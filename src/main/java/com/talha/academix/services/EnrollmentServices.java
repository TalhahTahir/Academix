package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.EnrollmentDTO;

public interface EnrollmentServices {
    //business logics
    EnrollmentDTO enrollStudentInCourse(Long studentId, Long courseId);

    //Simple CRUD for Testing and ADMIN use
    EnrollmentDTO enrollStudent(EnrollmentDTO dto);
    List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId); 
    List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId);
    void deleteEnrollment(Long id);

    public boolean updateCourseCompletionPercentage(Long studentId, Long courseId);
    public boolean courseCompletion (Long enrollmentId);
}
