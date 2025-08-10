package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.DocumentProgress;

public interface DocumentProgressRepo extends JpaRepository<DocumentProgress, Long> {
    Optional<DocumentProgress> findByEnrollmentIdAndDocumentId(Long enrollmentId, Long documentId);
    List<DocumentProgress> findByEnrollmentId(Long enrollmentId);

    @Query("SELECT COUNT(dp) FROM DocumentProgress dp WHERE dp.enrollment.enrollmentID = :enrollmentId AND dp.completed = true AND dp.document.content.course.courseid = :courseId")
    long countCompletedByEnrollmentAndCourse(@Param("enrollmentId") Long enrollmentId, @Param("courseId") Long courseId);
}
