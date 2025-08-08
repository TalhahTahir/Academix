package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCatagory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.model.User;

public interface CourseService {
    CourseDTO createCourse(CourseDTO dto);

    CourseDTO createCourseByTeacher(Long userid, CourseDTO dto);

    Boolean courseRejection(User admin, Long courseId);

    CourseDTO courseModification(User Teacher, Long courseId, CourseDTO dto);
    
    Boolean courseApproval(User admin, Long courseId);
    
    Boolean courseDevelopment(User Teacher, Long courseId);

    Boolean courseLaunch(User Teacher, Long courseId);

    CourseDTO courseDisable(User admin, Long courseId);

    CourseDTO getCourseById(Long courseId);
    
    List<CourseDTO> getCourseByCatagory(CourseCatagory catagory);
    
    List<CourseDTO> getAllCourses();
    
    List<CourseDTO> getAllCoursesByTeacher(Long teacherId);
    
    List<CourseDTO> getAllCoursesByState(CourseState state);

    CourseDTO updateCourse(Long courseId, CourseDTO dto);

    CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto);

    void deleteCourse(Long courseId);

    void deleteCourseByTeacher(Long userid, Long courseId);

    void deleteCourseByAdmin(Long userid, Long courseId);

    boolean teacherValidation(Long userid, Long courseId);

}