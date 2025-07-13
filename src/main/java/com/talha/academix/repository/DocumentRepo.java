package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Content;
import com.talha.academix.model.Document;

public interface DocumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByContent(Content content);
    int countByContent(Content content);

    long countByContent_Course_Courseid(Long courseId);
}
