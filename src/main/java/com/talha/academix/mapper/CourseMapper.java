package com.talha.academix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.CourseViewDTO;
import com.talha.academix.model.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(source = "teacher.userid", target = "teacherid")
    CourseDTO toDto(Course course);

    @Mapping(source = "teacherid", target = "teacher.userid")
    Course toEntity(CourseDTO dto);

    @Mapping(source = "teacher.userid", target = "teacherid")
    CourseViewDTO toViewDto(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "teacherid", target = "teacher.userid")
    void updateCourseFromDto(CourseDTO dto, @MappingTarget Course entity);
}
