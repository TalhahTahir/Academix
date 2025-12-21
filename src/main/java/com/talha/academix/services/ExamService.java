package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;

public interface ExamService {
    ExamResponse createExam(Long teacherId, CreateExamRequest req);
    ExamResponse getExamById(Long examId);
    List<ExamResponse> getExamsByCourse(Long courseId);
    ExamResponse updateExam(Long teacherId, Long examId, CreateExamRequest req);
    void deleteExam(Long teacherId, Long examId);
}