// DocumentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.mapper.DocumentMapper;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final ContentRepo contentRepo;
    private final CourseService courseService;
    private final StoredFileRepo storedFileRepo;
    private final ModelMapper mapper;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentDTO addDocument(Long userid, DocumentDTO dto) {
        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (!courseService.teacherOwnership(userid, content.getCourse().getCourseId())) {
            throw new RoleMismatchException("Only Owner can add document");
        }

        StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

        if (!file.getContent().getContentId().equals(content.getContentId())) {
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
        document.setStoredFile(file);
        document = documentRepo.save(document);

        DocumentDTO out = documentMapper.toDto(document);
        return out;
    }

    @Override
    public DocumentDTO updateDocument(Long userid, Long documentId, DocumentDTO dto) {
        Document existing = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        if (courseService.teacherOwnership(userid, existing.getContent().getCourse().getCourseId())) {

            mapper.getConfiguration().setSkipNullEnabled(true);
            mapper.map(dto, existing);

            if (!existing.getContent().getContentId().equals(dto.getContentId())) {
                Content content = contentRepo.findById(dto.getContentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
                existing.setContent(content);
            }

            existing = documentRepo.save(existing);

            return documentMapper.toDto(existing);
        } else {
            throw new RoleMismatchException("Only teacher can update document");
        }
    }

    @Override
    public DocumentDTO getDocumentById(Long documentId) {
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));
        return documentMapper.toDto(document);
    }

    @Override
    public List<DocumentDTO> getDocumentsByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return documentRepo.findByContent(content).stream()
                .map(d -> documentMapper.toDto(d))
                .toList();
    }

    @Override
    public void deleteDocument(Long userid, Long documentId) {
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentId));

        Content content = contentRepo.findById(document.getContent().getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseId())) {
            documentRepo.delete(document);
        } else {
            throw new RoleMismatchException("Only teacher can delete document");
        }
    }
}
