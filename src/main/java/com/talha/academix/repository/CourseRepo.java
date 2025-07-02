package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long>{

    public Course findByContentID(Long contentId);
    
        @Query("SELECT d.contentId FROM course d WHERE d.courseId = :courseId")
    Long findContentIdByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT d.contentId FROM course d WHERE d.examId = :examId")
    Long findContentIdByExamId(Long examId);
}
