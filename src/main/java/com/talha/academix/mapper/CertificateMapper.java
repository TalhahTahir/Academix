package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.model.Certificate;

@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(source = "course.courseid", target = "courseId")
    @Mapping(source = "student.userid", target = "studentId")
    @Mapping(source = "teacher.userid", target = "teacherId")
    CertificateDTO toDto(Certificate certificate);

}
