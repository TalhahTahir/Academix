package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.talha.academix.model.Exam;

public interface ExamRepo extends JpaRepository<Exam, Long> {
}
