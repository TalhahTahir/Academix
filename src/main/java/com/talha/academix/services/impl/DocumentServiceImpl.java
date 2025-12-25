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
import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.enums.StoredFileType;
import com.talha.academix.model.StoredFile;
import com.talha.academix.repository.StoredFileRepo;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final ContentRepo contentRepo;
    private final CourseService courseService;
    private final StoredFileRepo storedFileRepo;
    private final StoredFileService storedFileService;
    private final ModelMapper mapper;

    @Override
    public DocumentDTO addDocument(Long userid, DocumentDTO dto) {
        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (!courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {
            throw new RoleMismatchException("Only Owner can add document");
        }

        StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

        if (!file.getContent().getContentID().equals(content.getContentID())) {
            throw new RoleMismatchException("StoredFile does not belong to this content");
        }

        if (file.getType() != StoredFileType.DOCUMENT) {
            throw new RoleMismatchException("StoredFile type must be DOCUMENT");
        }
        if (file.getStatus() != StoredFileStatus.READY) {
            throw new RoleMismatchException("StoredFile must be READY before linking");
        }

        Document document = new Document();
        document.setContent(content);
        document.setTitle(dto.getTitle());
        document.setDescription(dto.getDescription());
        document.setFilePath(file);
        document = documentRepo.save(document);

        DocumentDTO out = new DocumentDTO();
        out.setDocumentId(document.getDocumentId());
        out.setContentId(content.getContentID());
        out.setTitle(document.getTitle());
        out.setDescription(document.getDescription());
        out.setStoredFileId(file.getId());
        out.setFileSignedUrl(storedFileService.getSignedDownloadUrl(file.getId(), 600).getSignedDownloadUrl());
        return out;
    }

    @Override
    public DocumentDTO updateDocument(Long userid, Long documentId, DocumentDTO dto) {
        Document existing = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        if (courseService.teacherOwnership(userid, existing.getContent().getCourse().getCourseid())) {

            mapper.getConfiguration().setSkipNullEnabled(true);
            mapper.map(dto, existing);

            if (!existing.getContent().getContentID().equals(dto.getContentId())) {
                Content content = contentRepo.findById(dto.getContentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
                existing.setContent(content);
            }

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
