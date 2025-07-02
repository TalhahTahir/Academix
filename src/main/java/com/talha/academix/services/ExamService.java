package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.ExamDTO;

public interface ExamService {
    ExamDTO addExam(ExamDTO dto);
    ExamDTO updateExam(Long examId, ExamDTO dto);
    ExamDTO getExamById(Long examId);
    List<ExamDTO> getExamsByCourse(Long courseId);
    void deleteExam(Long examId);

    ExamDTO createExamByTeacher(Long teacherId, ExamDTO dto);
    ExamDTO updateExamByTeacher(Long teacherId, Long examId, ExamDTO dto);
    void deleteExamByTeacher(Long teacherId, Long examId);

}
