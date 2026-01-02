package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.model.Attempt;

@Mapper(componentModel = "spring")
public interface AttemptMapper {
    
    @Mapping(source = "exam.id", target = "examId")
    @Mapping(source = "student.userid", target = "studentId")
    @Mapping(target = "answerIds", ignore = true)
    AttemptDTO toDto(Attempt attempt);

    @Mapping(source = "examId", target = "exam.id")
    @Mapping(source = "studentId", target = "student.userid")
    @Mapping(target = "answers", ignore = true)
    Attempt toEntity(AttemptDTO dto);
}
