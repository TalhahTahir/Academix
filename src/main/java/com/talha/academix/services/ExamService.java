package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.ExamDTO;

public interface ExamService {
    ExamDTO createExam(Long teacherId, ExamDTO dto);
    ExamDTO getExamById(Long examId);
    List<ExamDTO> getExamsByCourse(Long courseId);
    ExamDTO updateExam(Long examId, ExamDTO dto);
    void deleteExam(Long examId);
}