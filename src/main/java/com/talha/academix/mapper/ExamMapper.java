package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.ExamDTO;
import com.talha.academix.model.Exam;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class})
public interface ExamMapper {
    
    @Mapping(source = "id", target = "examId")
    @Mapping(source = "course.courseId", target = "courseId")
    ExamDTO toDto(Exam exam);

    @Mapping(source = "examId", target = "id")
    @Mapping(source = "courseId", target = "course.courseId")
    Exam toEntity(ExamDTO dto);
}
