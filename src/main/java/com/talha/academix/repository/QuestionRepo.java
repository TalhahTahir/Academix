package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Question;

public interface  QuestionRepo extends JpaRepository<Question, Long> {

    List<Question> findByExamId(Long examId);
    
}
