package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Document;

public interface DocumentRepo extends JpaRepository<Document, Long> {
}
