// DocumentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Document;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.DocumentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final ContentRepo contentRepo;
    private final CourseService courseService;
    private final ModelMapper mapper;

    @Override
    public DocumentDTO addDocument(Long userid, DocumentDTO dto) {
        Content content = contentRepo.findById(dto.getContentId())
            .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {
            Document document = new Document();
            document.setContent(content);
            document.setTitle(dto.getTitle());
            document.setFilePath(dto.getFilePath());
            document = documentRepo.save(document);

            return mapper.map(document, DocumentDTO.class);
        } else {
            throw new RoleMismatchException("Only Owner can add document");
        }
    }

    @Override
    public DocumentDTO updateDocument(Long userid, Long documentId, DocumentDTO dto) {
        Document existing = documentRepo.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        if (!existing.getContent().getContentID().equals(dto.getContentId())) {
            Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
            existing.setContent(content);
        }

        if (courseService.teacherOwnership(userid, existing.getContent().getCourse().getCourseid())) {
            existing.setTitle(dto.getTitle());
            existing.setFilePath(dto.getFilePath());
            existing = documentRepo.save(existing);

            return mapper.map(existing, DocumentDTO.class);
        } else {
            throw new RoleMismatchException("Only teacher can update document");
        }
    }

    @Override
    public DocumentDTO getDocumentById(Long documentId) {
        Document document = documentRepo.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
        return mapper.map(document, DocumentDTO.class);
    }

    @Override
    public List<DocumentDTO> getDocumentsByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return documentRepo.findByContent(content).stream()
            .map(d -> mapper.map(d, DocumentDTO.class))
            .toList();
    }

    @Override
    public void deleteDocument(Long userid, Long documentId) {
        Document document = documentRepo.findById(documentId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        Content content = contentRepo.findById(document.getContent().getContentID())
            .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {
            documentRepo.delete(document);
        } else {
            throw new RoleMismatchException("Only teacher can delete document");
        }
    }
}
