package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.services.DocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    @PreAuthorize("@contentSecurity.isContentOwner(principal, #dto.contentId)")
    @PostMapping
    public DocumentDTO addDocument(@Valid @RequestBody DocumentDTO dto) {
        return documentService.addDocument(dto);
    }

    @PreAuthorize("@documentSecurity.isDocumentOwner(principal, #documentId)")
    @PutMapping("/{documentId}")
    public DocumentDTO updateDocument(@PathVariable Long documentId,
            @Valid @RequestBody DocumentDTO dto) {
        return documentService.updateDocument(documentId, dto);
    }

    @GetMapping("{id}")
    public DocumentDTO getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping("content/{id}")
    public List<DocumentDTO> getDocumentsByContent(@PathVariable Long id) {
        return documentService.getDocumentsByContent(id);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("@documentSecurity.isDocumentOwner(principal, #documentId)")
    public void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }

}
