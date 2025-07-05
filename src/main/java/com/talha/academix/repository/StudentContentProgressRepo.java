package com.talha.academix.repository;

import com.talha.academix.model.StudentContentProgress;
import com.talha.academix.model.User;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentContentProgressRepo extends JpaRepository<StudentContentProgress, Long> {
    List<StudentContentProgress> findByStudent(User student);
    List<StudentContentProgress> findByContent(Content content);
    boolean existsByStudentAndContent(User student, Content content);
     @Query("SELECT COUNT(p) FROM StudentContentProgress p WHERE p.student = :student AND p.content.course = :course AND p.status = 'COMPLETED'")
    int countCompletedByStudentAndCourse(@Param("student") User student, @Param("course") Course course);

}
