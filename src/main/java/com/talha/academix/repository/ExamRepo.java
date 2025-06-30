package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Exam;

public interface ExamRepo extends JpaRepository<Exam, Long> {

    public List<Exam> findByCourseId(Long courseId);
}
