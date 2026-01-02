package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    
    @Mapping(source = "question.id", target = "questionId")
    @Mapping(source = "correct", target = "correct")
    OptionDTO toDto(QuestionOption option);

    @Mapping(source = "questionId", target = "question.id")
    @Mapping(source = "correct", target = "correct")
    QuestionOption toEntity(OptionDTO dto);
}
