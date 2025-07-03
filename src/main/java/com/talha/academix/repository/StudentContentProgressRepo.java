package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.talha.academix.model.StudentContentProgress;

public interface StudentContentProgressRepo extends JpaRepository<StudentContentProgress, Long> {

    List<StudentContentProgress> findByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT COUNT(p) FROM StudentContentProgress p WHERE p.studentId = :studentId AND p.courseId = :courseId AND p.status = 'COMPLETED'")
    int countCompletedByStudentAndCourse(Long studentId, Long courseId);
}
