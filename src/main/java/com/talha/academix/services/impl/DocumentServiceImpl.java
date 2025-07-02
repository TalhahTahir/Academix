package com.talha.academix.services.impl;

import com.talha.academix.dto.DocumentDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Document;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.services.DocumentService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepo documentRepo;
    private final CourseRepo courseRepo;
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

        TeacherAuth(teacherId, dto.getContentId());
        Document document = modelMapper.map(dto, Document.class);
        document = documentRepo.save(document);
        return modelMapper.map(document, DocumentDTO.class);

    }

    @Override
    public DocumentDTO updateDocByTeacher(Long teacherId, Long documentId, DocumentDTO dto) {

        TeacherAuth(teacherId, dto.getContentId());
        Document existing = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        existing.setTitle(dto.getTitle());
        existing.setFilePath(dto.getFilePath());
        existing.setContentId(dto.getContentId());
        existing = documentRepo.save(existing);

        return modelMapper.map(existing, DocumentDTO.class);
    }

    @Override
    public void deleteDocByTeacher(Long teacherId, Long documentId) {
        Long contentId = documentRepo.findContentIdByDocumentId(documentId);
        TeacherAuth(teacherId, contentId);
    
        Document document = documentRepo.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
        documentRepo.delete(document);
    }

    public boolean TeacherAuth(Long teacherId, Long contentId) {
        Course course = courseRepo.findByContentID(contentId);
        if (course.getTeacherid() != teacherId) {
            throw new RoleMismatchException("Unauthorized Teacher");
        }
        return true;
    }

}
