package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCatagory;

public interface CourseService {
    CourseDTO createCourse(CourseDTO dto);
    CourseDTO createCourseByAdmin(Long userid, CourseDTO dto);

    CourseDTO getCourseById(Long courseId);
    List<CourseDTO> getCourseByCatagory(CourseCatagory catagory);
    List<CourseDTO> getAllCourses();

    CourseDTO updateCourse(Long courseId, CourseDTO dto);
    CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto);

    void deleteCourse(Long courseId);
    void deleteCourseByAdmin(Long userid, Long courseId);

    boolean teacherValidation(Long userid, Long courseId);

}