package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.CourseViewDTO;
import com.talha.academix.dto.CreateCourseDTO;
import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;

public interface CourseService {
    CourseDTO createCourse(CreateCourseDTO dto);

    // CourseDTO createCourseByTeacher(Long userid, CourseDTO dto);

    CourseDTO courseRejection(Long courseId);

    CourseDTO courseModification(Long teacherId, Long courseId, CourseDTO dto);

    CourseDTO courseApproval(Long courseId);

    CourseDTO courseDevelopment(Long teacherId, Long courseId);

    CourseDTO courseLaunch(Long teacherId, Long courseId);

    CourseDTO courseDisable(Long courseId);

    CourseDTO getCourseById(Long courseId);

    List<CourseDTO> getCourseByCategory(CourseCategory category);

    List<CourseDTO> getAllCourses();

    List<CourseViewDTO>viewAllCourses(Long studentId);

    List<CourseDTO> getAllCoursesByTeacher(Long teacherId);

    List<CourseDTO> getAllCoursesByState(CourseState state);

    CourseDTO updateCourse(Long courseId, CourseDTO dto);

    // CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto);

    void deleteCourse(Long courseId);

    // void deleteCourseByTeacher(Long userid, Long courseId);

    // void deleteCourseByAdmin(Long userid, Long courseId);

    boolean teacherOwnership(Long userid, Long courseId);

    Long countAll();
}