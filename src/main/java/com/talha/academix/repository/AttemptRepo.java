package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Attempt;

public interface AttemptRepo extends JpaRepository<Attempt, Long>{

    List<Attempt> findByStudentId(Long studentId);

    List<Attempt> findByExamId(Long examId);
    
}
