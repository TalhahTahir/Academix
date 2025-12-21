package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Question;

public interface QuestionRepo extends JpaRepository<Question, Long> {

    // Was derived (invalid). Explicit JPQL using exam.id
    @Query("SELECT q FROM Question q WHERE q.exam.id = :examId")
    List<Question> findByExamId(@Param("examId") Long examId);

    long countByExamId(Long examId);
    
}
