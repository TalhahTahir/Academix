package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.DocumentProgress;

public interface DocumentProgressRepo extends JpaRepository<DocumentProgress, Long> {
    Optional<DocumentProgress> findByEnrollmentIdAndDocumentId(Long enrollmentId, Long documentId);
    List<DocumentProgress> findByEnrollmentId(Long enrollmentId);
}
