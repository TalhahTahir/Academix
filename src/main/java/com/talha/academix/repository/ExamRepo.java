package com.talha.academix.repository;

import com.talha.academix.model.Exam;
import com.talha.academix.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepo extends JpaRepository<Exam, Long> {
    List<Exam> findByCourse(Course course);
}
