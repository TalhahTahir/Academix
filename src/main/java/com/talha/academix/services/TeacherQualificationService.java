package com.talha.academix.services;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.enums.Degree;

import java.util.List;

public interface TeacherQualificationService {
    TeacherQualificationDTO addQualification(TeacherQualificationDTO dto);
    TeacherQualificationDTO updateQualification(Long qualificationId, TeacherQualificationDTO dto);
    TeacherQualificationDTO getQualificationById(Long qualificationId);
    List<TeacherQualificationDTO> getQualificationsByTeacher(Long teacherId);
    List<TeacherQualificationDTO> getQualificationsByDegree(Degree degree);
    void deleteQualification(Long qualificationId);
}
