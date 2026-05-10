package com.talha.academix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.model.TeacherQualification;

@Mapper(componentModel = "spring")
public interface TeacherQualificationMapper {
    
    @Mapping(source = "teacher.userid", target = "teacherId")
    TeacherQualificationDTO toDto(TeacherQualification qualification);

    @Mapping(source = "teacherId", target = "teacher.userid")
    TeacherQualification toEntity(TeacherQualificationDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateQuestionFromDto(TeacherQualificationDTO dto, @MappingTarget TeacherQualification t);
}
