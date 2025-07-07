package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Course;
import com.talha.academix.model.Exam;

public interface ExamRepo extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);

    public List<Exam> findByCourseId(Long courseId);
}
