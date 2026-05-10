// ContentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.enums.CourseState;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.mapper.ContentMapper;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.ContentService;
import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.enums.StoredFileType;
import com.talha.academix.model.StoredFile;
import com.talha.academix.repository.StoredFileRepo;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

        private final ContentRepo contentRepo;
        private final CourseRepo courseRepo;
        private final ContentMapper contentMapper;
        private final StoredFileRepo storedFileRepo;
        private final StoredFileService storedFileService;

        @Override
        public ContentDTO addContent(ContentDTO dto) {

                Course course = courseRepo.findById(dto.getCourseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with id: " + dto.getCourseId()));

                if ((course.getState() == CourseState.DRAFT
                                || course.getState() == CourseState.IN_DEVELOPMENT
                                || course.getState() == CourseState.MODIFIED
                                || course.getState() == CourseState.REJECTED)) {

                        Content content = new Content();
                        content.setCourse(course);
                        content.setDescription(dto.getDescription());
                        // DO NOT set image here (imageFile will be linked later)
                        content = contentRepo.save(content);

                        // manual DTO mapping to avoid ModelMapper confusion with lazy relations
                        ContentDTO out = new ContentDTO();
                        out.setContentId(content.getContentId());
                        out.setCourseId(course.getCourseId());
                        out.setDescription(content.getDescription());
                        out.setImageFileId(null);
                        out.setImageSignedUrl(null);

                        return out;
                } else {
                        throw new RoleMismatchException(
                                        "Owner can only add content in DRAFT, IN_DEVELOPMENT or REJECTED courses");
                }
        }

        @Override
        public ContentDTO updateContent(Long contentId, ContentDTO dto) {

                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Content not found with id: " + contentId));

                content.setCourse(courseRepo.findById(dto.getCourseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with id: " + dto.getCourseId())));

                if (!dto.getDescription().isEmpty()) {
                        content.setDescription(dto.getDescription());
                }
                content = contentRepo.save(content);
                return contentMapper.toDto(content);
        }

        @Override
        public ContentDTO getContentById(Long contentId) {
                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));

                ContentDTO dto = contentMapper.toDto(content);
                dto.setCourseId(content.getCourse().getCourseId());

                if (content.getImageFile() != null) {
                        dto.setImageFileId(content.getImageFile().getId());
                        dto.setImageSignedUrl(
                                        storedFileService.getSignedDownloadUrl(content.getImageFile().getId(), 600)
                                                        .getSignedDownloadUrl());
                }
                return dto;
        }

        @Override
        public List<ContentDTO> getContentByCourse(Long courseId) {
                Course course = courseRepo.findById(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
                List<Content> list = contentRepo.findByCourse(course);
                return list.stream()
                                .map(c -> contentMapper.toDto(c))
                                .toList();
        }

        @Override
        public void deleteContent(Long contentId) {
                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
                contentRepo.delete(content);
        }

        @Override
        public ContentDTO setContentImage(Long contentId, Long storedFileId) {

                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));

                Long courseId = content.getCourse().getCourseId();

                StoredFile file = storedFileRepo.findById(storedFileId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "StoredFile not found: " + storedFileId));

                // must be from same course
                if (!file.getContent().getContentId().equals(content.getContentId())) {
                        throw new RoleMismatchException("StoredFile does not belong to this content");
                }
                // must be correct type
                if (file.getType() != StoredFileType.CONTENT_IMAGE) {
                        throw new RoleMismatchException("StoredFile type must be CONTENT_IMAGE");
                }

                // must be ready
                if (file.getStatus() != StoredFileStatus.READY) {
                        throw new RoleMismatchException("StoredFile must be READY before linking");
                }

                content.setImageFile(file);
                content = contentRepo.save(content);

                // map to DTO
                ContentDTO dto = contentMapper.toDto(content);

                // manually set ids + signed URL
                dto.setCourseId(courseId);
                dto.setImageFileId(file.getId());
                dto.setImageSignedUrl(storedFileService.getSignedDownloadUrl(file.getId(), 600).getSignedDownloadUrl());

                return dto;
        }
}