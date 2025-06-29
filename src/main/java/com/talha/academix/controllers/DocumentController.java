package com.talha.academix.controllers;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public DocumentDTO addDocument(@RequestBody DocumentDTO dto) {
        return documentService.addDocument(dto);
    }

    @PutMapping("/{documentId}")
    public DocumentDTO updateDocument(@PathVariable Long documentId, @RequestBody DocumentDTO dto) {
        return documentService.updateDocument(documentId, dto);
    }

    @GetMapping("/{documentId}")
    public DocumentDTO getDocumentById(@PathVariable Long documentId) {
        return documentService.getDocumentById(documentId);
    }

    @GetMapping("/content/{contentId}")
    public List<DocumentDTO> getDocumentsByContent(@PathVariable Long contentId) {
        return documentService.getDocumentsByContent(contentId);
    }

    @DeleteMapping("/{documentId}")
    public void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }
}
