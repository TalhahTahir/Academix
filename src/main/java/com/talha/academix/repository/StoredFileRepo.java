package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.StoredFile;

public interface StoredFileRepo extends JpaRepository<StoredFile, Long> {
    List<StoredFile> findByContent_ContentID(Long contentId);
    boolean existsByObjectKey(String objectKey);
}