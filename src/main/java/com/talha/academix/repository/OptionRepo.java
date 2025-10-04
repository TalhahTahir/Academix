package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.QuestionOption;

public interface OptionRepo extends JpaRepository<QuestionOption, Long> {

    // Explicit JPQL for nested question.id
    @Query("SELECT o FROM QuestionOption o WHERE o.question.id = :questionId")
    List<QuestionOption> findByQuestionId(@Param("questionId") Long questionId);

    // Explicit JPQL for count of correct options of a question
    @Query("SELECT COUNT(o) FROM QuestionOption o WHERE o.question.id = :questionId AND o.isCorrect = true")
    long countByQuestionIdAndIsCorrectTrue(@Param("questionId") Long questionId);
    
}
