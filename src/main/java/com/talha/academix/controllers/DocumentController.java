package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.services.DocumentService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("teachers/{id}")
    public DocumentDTO addDocument(@RequestParam Long id, @RequestParam DocumentDTO dto) {
        return documentService.addDocument(id, dto);
    }

    @PutMapping("teachers/{teacherId}/documents/{documentId}")
    public DocumentDTO updateDocument(@PathVariable Long teacherId, @PathVariable Long documentId,
            @RequestBody DocumentDTO dto) {
        return documentService.updateDocument(teacherId, documentId, dto);
    }

    @GetMapping("{id}")
    public DocumentDTO getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @GetMapping("content/{id}")
    public List<DocumentDTO> getDocumentsByContent(@PathVariable Long id) {
        return documentService.getDocumentsByContent(id);
    }

    @DeleteMapping("teachers/{teacherId}/documents/{documentId}")
    public void deleteDocument(@PathVariable Long teacherId, @PathVariable Long documentId) {
        documentService.deleteDocument(teacherId, documentId);
    }

}
