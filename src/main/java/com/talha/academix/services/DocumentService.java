package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.DocumentDTO;

public interface DocumentService {
    DocumentDTO addDocument(DocumentDTO dto);
    DocumentDTO updateDocument(Long documentId, DocumentDTO dto);
    DocumentDTO getDocumentById(Long documentId);
    List<DocumentDTO> getDocumentsByContent(Long contentId);
    void deleteDocument(Long documentId);
}