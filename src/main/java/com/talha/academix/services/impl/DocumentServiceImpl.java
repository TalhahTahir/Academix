package com.talha.academix.services.impl;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Document;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.ContentService;
import com.talha.academix.services.DocumentService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.talha.academix.enums.ActivityAction;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final ContentService contentService;
    private final ContentRepo contentRepo;
    private final ActivityLogService activityLogService;
    private final ModelMapper modelMapper;

    @Override
    public DocumentDTO addDocument(DocumentDTO dto) {
        Document document = modelMapper.map(dto, Document.class);
        document = documentRepo.save(document);
        return modelMapper.map(document, DocumentDTO.class);
    }

    @Override
    public DocumentDTO updateDocument(Long documentId, DocumentDTO dto) {
        Document existing = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        existing.setTitle(dto.getTitle());
        existing.setFilePath(dto.getFilePath());
        existing.setContentId(dto.getContentId());
        existing = documentRepo.save(existing);
        return modelMapper.map(existing, DocumentDTO.class);
    }

    @Override
    public DocumentDTO getDocumentById(Long documentId) {
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        return modelMapper.map(document, DocumentDTO.class);
    }

    @Override
    public List<DocumentDTO> getDocumentsByContent(Long contentId) {
        List<Document> documents = documentRepo.findByContentID(contentId);
        return documents.stream()
                .map(d -> modelMapper.map(d, DocumentDTO.class))
                .toList();
    }

    @Override
    public void deleteDocument(Long documentId) {
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        documentRepo.delete(document);
    }

    @Override
    public DocumentDTO addDocByTeacher(Long teacherId, DocumentDTO dto) {

        Long contentId = dto.getContentId();
        Long courseId = contentRepo.findCourseIdByContentId(contentId);

        if (contentService.verifyTeacher(teacherId, courseId)) {

            activityLogService.logAction(teacherId,
             ActivityAction.CONTENT_UPLOAD,
             "Teacher "+ teacherId +" uploaded a document to the course: " + courseId);
             
            return addDocument(dto);
        } else
            throw new ForbiddenException("Teacher is not authorized to upload Document");
    }

    @Override
    public void deleteDocByTeacher(Long teacherId, Long documentId) {

        Long contentId = getDocumentById(documentId).getContentId();
        Long courseId = contentRepo.findCourseIdByContentId(contentId);

        if (contentService.verifyTeacher(teacherId, courseId)) {
            deleteDocument(documentId);
        } else
            throw new ForbiddenException("Teacher is not authorized to delete Document");
    }
}
