package com.talha.academix.services;

public interface ProgressService {
    void markLectureCompleted(Long enrollmentId, Long lectureId);
    void markDocumentCompleted(Long enrollmentId, Long documentId);
    double computeCourseCompletionPercentage(Long enrollmentId);
}
