package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Course;
import com.talha.academix.model.Exam;

public interface ExamRepo extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);

    // Explicit JPQL for nested course.courseid
    @Query("SELECT e FROM Exam e WHERE e.course.courseid = :courseId")
    List<Exam> findByCourseId(@Param("courseId") Long courseId);
}
