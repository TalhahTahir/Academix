package com.talha.academix.repository;

import com.talha.academix.model.Document;
import com.talha.academix.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepo extends JpaRepository<Document, Long> {
    List<Document> findByContent(Content content);
    int countByContent(Content content);
}
