package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.AttemptAnswerDTO;
import com.talha.academix.model.AttemptAnswer;

@Mapper(componentModel = "spring")
public interface AttemptAnswerMapper {
    
    @Mapping(source = "attempt.id", target = "attemptId")
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "selectedOption.id", target = "selectedOptionId")
    AttemptAnswerDTO toDto(AttemptAnswer attemptAnswer);

    @Mapping(source = "attemptId", target = "attempt.id")
    @Mapping(source = "questionId", target = "question.id")
    @Mapping(source = "selectedOptionId", target = "selectedOption.id")
    AttemptAnswer toEntity(AttemptAnswerDTO dto);
}