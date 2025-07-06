package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.TeacherQualificationDTO;

public interface TeacherQualificationService {
    TeacherQualificationDTO addQualification(TeacherQualificationDTO dto);
    TeacherQualificationDTO updateQualification(Long qualificationId, TeacherQualificationDTO dto);
    TeacherQualificationDTO getQualificationById(Long qualificationId);
    List<TeacherQualificationDTO> getQualificationsByTeacher(Long teacherId);
    List<TeacherQualificationDTO> getQualificationsByDegree(String degree);
    void deleteQualification(Long qualificationId);
}