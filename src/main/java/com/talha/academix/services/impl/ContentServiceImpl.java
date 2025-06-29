package com.talha.academix.services.impl;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Content;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.services.ContentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepo contentRepo;
    private final ModelMapper modelMapper;

    @Override
    public ContentDTO addContent(ContentDTO dto) {
        Content content = modelMapper.map(dto, Content.class);
        content = contentRepo.save(content);
        return modelMapper.map(content, ContentDTO.class);
    }

    @Override
    public ContentDTO updateContent(Long contentId, ContentDTO dto) {
        Content existing = contentRepo.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));
        // update fields
        existing.setDescription(dto.getDescription());
        existing.setImage(dto.getImage());
        existing.setCourseID(dto.getCourseID());
        existing = contentRepo.save(existing);
        return modelMapper.map(existing, ContentDTO.class);
    }

    @Override
    public ContentDTO getContentById(Long contentId) {
        Content content = contentRepo.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));
        return modelMapper.map(content, ContentDTO.class);
    }

    @Override
    public List<ContentDTO> getContentByCourse(Long courseId) {
        List<Content> contents = contentRepo.findByCourseID(courseId);
        return contents.stream()
                .map(c -> modelMapper.map(c, ContentDTO.class))
                .toList();
    }

    @Override
    public void deleteContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));
        contentRepo.delete(content);
    }
}
