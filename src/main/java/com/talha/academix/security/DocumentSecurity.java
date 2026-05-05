package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Document;
import com.talha.academix.repository.DocumentRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("documentSecurity")
public class DocumentSecurity {
    
    private final DocumentRepo documentRepo;

    public boolean isDocumentOwner(CustomUserDetails principal, Long documentId) {
        if (principal == null || documentId == null) {
            return false;
        }

        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        return document.getContent().getCourse().getTeacher().getUserid().equals(principal.getId());
    }
}
