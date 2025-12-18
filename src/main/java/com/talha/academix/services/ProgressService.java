package com.talha.academix.services;

import com.talha.academix.dto.EnrollmentStatsDTO;

public interface ProgressService {
    void markLectureCompleted(Long enrollmentId, Long lectureId);
    void markDocumentCompleted(Long enrollmentId, Long documentId);
    double computeCourseCompletionPercentage(Long enrollmentId);
    EnrollmentStatsDTO getEnrollmentStats(Long enrollmentId);
}
