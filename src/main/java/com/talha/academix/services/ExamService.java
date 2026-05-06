package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;

public interface ExamService {
    ExamResponse createExam(CreateExamRequest req);
    ExamResponse getExamById(Long examId);
    List<ExamResponse> getExamsByCourse(Long courseId);
    ExamResponse updateExam(Long examId, CreateExamRequest req);
    void deleteExam(Long examId);
}