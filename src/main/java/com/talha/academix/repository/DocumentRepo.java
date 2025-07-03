package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Document;

public interface DocumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByContentID(Long contentId);

    @Query("SELECT d.contentId FROM document d WHERE d.documentId = :documentId")
    Long findContentIdByDocumentId(@Param("documentId") Long documentId);

    public int countByCourseId(Long courseId);

}


