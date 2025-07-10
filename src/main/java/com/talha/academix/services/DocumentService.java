package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.DocumentDTO;

public interface DocumentService {
    DocumentDTO addDocument(Long userid, DocumentDTO dto);
    DocumentDTO updateDocument(Long userid, Long documentId, DocumentDTO dto);
    DocumentDTO getDocumentById(Long documentId);
    List<DocumentDTO> getDocumentsByContent(Long contentId);
    void deleteDocument(Long userid, Long documentId);
}
