package com.talha.academix.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.talha.academix.dto.QuestionDTO;
import com.talha.academix.model.Question;
import com.talha.academix.model.QuestionOption;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    
    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "options", target = "optionIds", qualifiedByName = "optionsToIds")
    QuestionDTO toDto(Question question);

    @Mapping(source = "examId", target = "exam.id")
    @Mapping(target = "options", ignore = true)
    Question toEntity(QuestionDTO dto);

    @Named("optionsToIds")
    default List<Long> optionsToIds(List<QuestionOption> options) {
        if (options == null) {
            return null;
        }
        return options.stream()
                .map(QuestionOption::getId)
                .collect(Collectors.toList());
    }
}
