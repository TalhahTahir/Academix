package com.talha.academix.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.QuestionOption;

public interface OptionRepo extends JpaRepository<QuestionOption, Long> {

    List<QuestionOption> findByQuestionId(Long questionId);

    long countByQuestionIdAndIsCorrectTrue(Long questionId);



    
}
