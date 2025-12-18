package com.talha.academix.services.impl;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Document;
import com.talha.academix.model.DocumentProgress;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Lecture;
import com.talha.academix.model.LectureProgress;
import com.talha.academix.repository.DocumentProgressRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.repository.LectureProgressRepo;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.ProgressService;
import com.talha.academix.dto.EnrollmentStatsDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final EnrollmentService enrollmentService;
    private final LectureRepo lectureRepo;
    private final DocumentRepo documentRepo;
    private final LectureProgressRepo lectureProgressRepo;
    private final DocumentProgressRepo documentProgressRepo;
    private final Clock clock; // inject java.time.Clock for testability

    @Override
    @Transactional
    public void markLectureCompleted(Long enrollmentId, Long lectureId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(enrollmentId);
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        // lock row to avoid race on creation
        LectureProgress progress = lectureProgressRepo.findByEnrollmentIdAndLectureIdForUpdate(enrollmentId, lectureId)
                .orElseGet(() -> new LectureProgress(/* id */ null, enrollment, lecture, false, null));

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(Instant.now(clock));
            lectureProgressRepo.save(progress);

            // update just the percentage (no DTO mapping)
            double percentage = computeCourseCompletionPercentage(enrollmentId);
            enrollmentService.updateCompletionPercentage(enrollmentId, percentage);
        }
    }

    @Override
    @Transactional
    public void markDocumentCompleted(Long enrollmentId, Long documentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(enrollmentId);
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        DocumentProgress progress = documentProgressRepo
                .findByEnrollmentIdAndDocumentId(enrollmentId, documentId)
                .orElseGet(() -> new DocumentProgress(null, enrollment, document, false, null));

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(Instant.now(clock));
            documentProgressRepo.save(progress);

            double percentage = computeCourseCompletionPercentage(enrollmentId);
            enrollmentService.updateCompletionPercentage(enrollmentId, percentage);
        }
    }

    @Override
    public double computeCourseCompletionPercentage(Long enrollmentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(enrollmentId);
        Long courseId = enrollment.getCourse().getCourseid();

        long totalLectures = lectureRepo.countByContent_Course_Courseid(courseId);
        long totalDocuments = documentRepo.countByContent_Course_Courseid(courseId);

        long completedLectures = lectureProgressRepo.countCompletedByEnrollmentAndCourse(enrollmentId, courseId);
        long completedDocuments = documentProgressRepo.countCompletedByEnrollmentAndCourse(enrollmentId, courseId);

        long totalItems = totalLectures + totalDocuments;
        long completedItems = completedLectures + completedDocuments;

        return totalItems == 0 ? 0.0 : ((double) completedItems / totalItems) * 100.0;
    }

    @Override
    public EnrollmentStatsDTO getEnrollmentStats(Long enrollmentId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(enrollmentId);
        Long courseId = enrollment.getCourse().getCourseid();

        long totalLectures = lectureRepo.countByContent_Course_Courseid(courseId);
        long totalDocuments = documentRepo.countByContent_Course_Courseid(courseId);

        long completedLectures = lectureProgressRepo.countCompletedByEnrollmentAndCourse(enrollmentId, courseId);
        long completedDocuments = documentProgressRepo.countCompletedByEnrollmentAndCourse(enrollmentId, courseId);

        long totalItems = totalLectures + totalDocuments;
        long completedItems = completedLectures + completedDocuments;

        double percentage = totalItems == 0 ? 0.0 : ((double) completedItems / totalItems) * 100.0;

        return EnrollmentStatsDTO.builder()
                .enrollmentId(enrollmentId)
                .courseId(courseId)
                .totalLectures(totalLectures)
                .completedLectures(completedLectures)
                .totalDocuments(totalDocuments)
                .completedDocuments(completedDocuments)
                .completionPercentage(percentage)
                .build();
    }
}
