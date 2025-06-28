package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;

public interface CourseService {
    
    public CourseDTO createCourse(CourseDTO dto);
    public CourseDTO updateCourse(Long id,CourseDTO dto);
    public void deleteCourse(Long id);
    public CourseDTO getCourseById(Long id);
    public List<CourseDTO> getAllCourses();
}
