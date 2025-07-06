package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.ContentDTO;

public interface ContentService {
    ContentDTO addContent(ContentDTO dto);
    ContentDTO updateContent(Long contentId, ContentDTO dto);
    ContentDTO getContentById(Long contentId);
    List<ContentDTO> getContentByCourse(Long courseId);
    void deleteContent(Long contentId);
}