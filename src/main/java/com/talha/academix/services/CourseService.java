package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;

public interface CourseService {
    CourseDTO createCourse(CourseDTO dto);
    CourseDTO getCourseById(Long courseId);
    List<CourseDTO> getAllCourses();
    CourseDTO updateCourse(Long courseId, CourseDTO dto);
    void deleteCourse(Long courseId);
}