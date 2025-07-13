package com.talha.academix.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Document;
import com.talha.academix.model.DocumentProgress;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Lecture;
import com.talha.academix.model.LectureProgress;
import com.talha.academix.repository.DocumentProgressRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.LectureProgressRepo;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.ProgressService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final EnrollmentRepo enrollmentRepo;
    private final LectureRepo lectureRepo;
    private final DocumentRepo documentRepo;
    private final LectureProgressRepo lectureProgressRepo;
    private final DocumentProgressRepo documentProgressRepo;

    private final EnrollmentService enrollmentService; // for updating completion %

    @Override
    @Transactional
    public void markLectureCompleted(Long enrollmentId, Long lectureId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found"));

        LectureProgress progress = lectureProgressRepo.findByEnrollmentIdAndLectureId(enrollmentId, lectureId)
                .orElse(new LectureProgress(null, enrollment, lecture, false, null));

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            lectureProgressRepo.save(progress);
        }

        updateEnrollmentCompletion(enrollment);
    }

    @Override
    @Transactional
    public void markDocumentCompleted(Long enrollmentId, Long documentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        DocumentProgress progress = documentProgressRepo.findByEnrollmentIdAndDocumentId(enrollmentId, documentId)
                .orElse(new DocumentProgress(null, enrollment, document, false, null));

        if (!Boolean.TRUE.equals(progress.getCompleted())) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            documentProgressRepo.save(progress);
        }

        updateEnrollmentCompletion(enrollment);
    }

    @Override
    public double computeCourseCompletionPercentage(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        Long courseId = enrollment.getCourse().getCourseid();
        long totalLectures = lectureRepo.countByContent_Course_Courseid(courseId);
        long totalDocuments = documentRepo.countByContent_Course_Courseid(courseId);

        long completedLectures = lectureProgressRepo.findByEnrollmentId(enrollmentId).stream()
                .filter(LectureProgress::getCompleted).count();
        long completedDocuments = documentProgressRepo.findByEnrollmentId(enrollmentId).stream()
                .filter(DocumentProgress::getCompleted).count();

        long totalItems = totalLectures + totalDocuments;
        long completedItems = completedLectures + completedDocuments;

        return totalItems == 0 ? 0.0 : ((double) completedItems / totalItems) * 100;
    }

    private void updateEnrollmentCompletion(Enrollment enrollment) {
        double percentage = computeCourseCompletionPercentage(enrollment.getEnrollmentID());
        enrollment.setCompletionPercentage(percentage);
        enrollmentRepo.save(enrollment);
    }
}

