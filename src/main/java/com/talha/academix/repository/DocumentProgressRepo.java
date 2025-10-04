package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.DocumentProgress;

public interface DocumentProgressRepo extends JpaRepository<DocumentProgress, Long> {
    
    @Query("SELECT dp FROM DocumentProgress dp WHERE dp.enrollment.enrollmentID = :enrollmentId AND dp.document.documentId = :documentId")
    Optional<DocumentProgress> findByEnrollmentIdAndDocumentId(@Param("enrollmentId") Long enrollmentId,
                                                               @Param("documentId") Long documentId);
    @Query("SELECT dp FROM DocumentProgress dp WHERE dp.enrollment.enrollmentID = :enrollmentId")
    List<DocumentProgress> findByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("SELECT COUNT(dp) FROM DocumentProgress dp WHERE dp.enrollment.enrollmentID = :enrollmentId AND dp.completed = true AND dp.document.content.course.courseid = :courseId")
    long countCompletedByEnrollmentAndCourse(@Param("enrollmentId") Long enrollmentId, @Param("courseId") Long courseId);
}
