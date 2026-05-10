package com.talha.academix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.talha.academix.dto.OptionDTO;
import com.talha.academix.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface OptionMapper {
    
    @Mapping(source = "question.id", target = "questionId")
    OptionDTO toDto(QuestionOption option);

    QuestionOption toEntity(OptionDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOptionFromDto(OptionDTO dto, @MappingTarget QuestionOption opt);
}
