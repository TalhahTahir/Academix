package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.ExamDTO;

public interface ExamService {
    ExamDTO createExam(Long teacherId, ExamDTO dto);
    ExamDTO getExamById(Long examId);
    List<ExamDTO> getExamsByCourse(Long courseId);
    ExamDTO updateExam(Long userid, Long examId, ExamDTO dto);
    void deleteExam(Long userid, Long examId);
    Double checkExam(AttemptDTO attempt);
}
