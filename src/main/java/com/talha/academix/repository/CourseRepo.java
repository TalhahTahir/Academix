package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {

    Course findCourseByCourseName(String courseName);

    List<Course> findAllByCategory(CourseCategory category);

    // Optional replacement if needed later (safe explicit JPQL)
    @Query("SELECT c FROM Course c WHERE c.courseId = :courseId AND c.teacher.userid = :teacherId")
    Course findByCourseIdAndTeacherUserid(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    boolean existsByCourseNameAndTeacher_Userid(String courseName, Long userid);

    List<Course> findAllByTeacher_Userid(Long userid);

    List<Course> findAllByState(CourseState state);

    long count();

    long countByState(CourseState state);

}
