// ContentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.ContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepo contentRepo;
    private final CourseRepo courseRepo;
    private final ActivityLogService activityLogService;
    private final ModelMapper mapper;

    @Override
    public ContentDTO addContent(ContentDTO dto) {
        Course course = courseRepo.findById(dto.getCourseID())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + dto.getCourseID()));

        Content content = new Content();
        content.setCourse(course);
        content.setDescription(dto.getDescription());
        content.setImage(dto.getImage());
        content = contentRepo.save(content);

        // after saving content:
        activityLogService.logAction(
                content.getCourse().getTeacher().getUserid(),
                ActivityAction.CONTENT_UPLOAD,
                "Content " + content.getContentID() + " uploaded/updated for Course "
                        + content.getCourse().getCourseid());

        return mapper.map(content, ContentDTO.class);
    }

    @Override
    public ContentDTO updateContent(Long contentId, ContentDTO dto) {
        Content existing = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));

        // only description and image are mutable
        existing.setDescription(dto.getDescription());
        existing.setImage(dto.getImage());
        existing = contentRepo.save(existing);

        // after saving content:
        activityLogService.logAction(
             existing.getCourse().getTeacher().getUserid(),
             ActivityAction.CONTENT_UPLOAD,
             "Content " + existing.getContentID() + " uploaded/updated for Course " + existing.getCourse().getCourseid());

        return mapper.map(existing, ContentDTO.class);
    }

    @Override
    public ContentDTO getContentById(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return mapper.map(content, ContentDTO.class);
    }

    @Override
    public List<ContentDTO> getContentByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        List<Content> list = contentRepo.findByCourse(course);
        return list.stream()
                .map(c -> mapper.map(c, ContentDTO.class))
                .toList();
    }

    @Override
    public void deleteContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        contentRepo.delete(content);
    }
}
