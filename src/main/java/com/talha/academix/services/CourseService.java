package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCatagory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.model.User;

public interface CourseService {
    CourseDTO createCourse(CourseDTO dto);

    CourseDTO createCourseByTeacher(Long userid, CourseDTO dto);

    CourseDTO getCourseById(Long courseId);

    List<CourseDTO> getCourseByCatagory(CourseCatagory catagory);

    List<CourseDTO> getAllCourses();

    List<CourseDTO> getAllCoursesByTeacher(Long teacherId);

    List<CourseDTO> getAllCoursesByState(CourseState state);

    Boolean courseRejection(User admin, Long courseId);

    Boolean courseApproval(User admin, Long courseId);

    CourseDTO updateCourse(Long courseId, CourseDTO dto);

    CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto);

    void deleteCourse(Long courseId);

    void deleteCourseByAdmin(Long userid, Long courseId);

    boolean teacherValidation(Long userid, Long courseId);

}