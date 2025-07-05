package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long>{

    public Course findByContentID(Long contentId);
    
    @Query("SELECT d.contentId FROM Course d WHERE d.courseId = :courseId")
    List<Long> findContentIdByCourseId(@Param("courseId") Long courseId);
    

    @Query("SELECT d.contentId FROM Course d WHERE d.examId = :examId")
    Long findContentIdByExamId(@Param("examId") Long examId);    

    @Query("SELECT c FROM Course c WHERE c.courseid = :courseId AND c.teacherid = :teacherId")
    public Course verifyTeacher(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
}
