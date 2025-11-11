package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.model.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(source = "teacher.userid", target = "teacherid")
    CourseDTO toDto(Course course);
}
