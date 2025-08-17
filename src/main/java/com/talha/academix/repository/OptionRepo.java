package com.talha.academix.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Option;

public interface OptionRepo extends JpaRepository<Option, Long> {

    List<Option> findByQuestionId(Long questionId);

    long countByQuestionIdAndIsCorrectTrue(Long questionId);



    
}
