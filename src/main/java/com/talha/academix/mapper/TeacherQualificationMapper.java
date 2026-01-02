package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.model.TeacherQualification;

@Mapper(componentModel = "spring")
public interface TeacherQualificationMapper {
    
    @Mapping(source = "teacher.userid", target = "teacherId")
    TeacherQualificationDTO toDto(TeacherQualification qualification);

    @Mapping(source = "teacherId", target = "teacher.userid")
    TeacherQualification toEntity(TeacherQualificationDTO dto);
}
