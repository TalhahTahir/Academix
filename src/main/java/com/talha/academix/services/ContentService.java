package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.ContentDTO;

public interface ContentService {
    ContentDTO addContent(Long userid, ContentDTO dto);
    ContentDTO updateContent(Long userid, Long contentId, ContentDTO dto);
    ContentDTO getContentById(Long contentId);
    List<ContentDTO> getContentByCourse(Long courseId);
    void deleteContent(Long userid, Long contentId);
    ContentDTO setContentImage(Long teacherId, Long contentId, Long storedFileId);
}