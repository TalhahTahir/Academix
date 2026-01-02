package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.model.Enrollment;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    
    @Mapping(source = "student.userid", target = "studentId")
    @Mapping(source = "course.courseId", target = "courseId")
    EnrollmentDTO toDto(Enrollment enrollment);

    @Mapping(source = "studentId", target = "student.userid")
    @Mapping(source = "courseId", target = "course.courseId")
    Enrollment toEntity(EnrollmentDTO dto);
}
