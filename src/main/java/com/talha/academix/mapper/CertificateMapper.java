package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.model.Certificate;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(source = "course.courseId", target = "courseId")
    @Mapping(source = "student.userid", target = "studentId")
    @Mapping(source = "teacher.userid", target = "teacherId")
    CertificateDTO toDto(Certificate certificate);

    @Mapping(source = "courseId", target = "course.courseId")
    @Mapping(source = "studentId", target = "student.userid")
    @Mapping(source = "teacherId", target = "teacher.userid")
    Certificate toEntity(CertificateDTO dto);
}
