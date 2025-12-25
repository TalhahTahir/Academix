// ContentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.enums.CourseState;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.ContentService;
import com.talha.academix.services.CourseService;
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
        private final CourseService courseService;
        private final ModelMapper mapper;
        private final StoredFileRepo storedFileRepo;
        private final StoredFileService storedFileService;
        Boolean owned;

        @Override
        public ContentDTO addContent(Long userid, ContentDTO dto) {

                Course course = courseRepo.findById(dto.getCourseID())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with id: " + dto.getCourseID()));

                owned = courseService.teacherOwnership(userid, course.getCourseid());

                if (owned && (course.getState() == CourseState.DRAFT
                                || course.getState() == CourseState.IN_DEVELOPMENT
                                || course.getState() == CourseState.REJECTED)) {

                        Content content = new Content();
                        content.setCourse(course);
                        content.setDescription(dto.getDescription());
                        // DO NOT set image here (imageFile will be linked later)
                        content = contentRepo.save(content);

                        // manual DTO mapping to avoid ModelMapper confusion with lazy relations
                        ContentDTO out = new ContentDTO();
                        out.setContentID(content.getContentID());
                        out.setCourseID(course.getCourseid());
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
        public ContentDTO updateContent(Long userid, Long contentId, ContentDTO dto) {

                owned = courseService.teacherOwnership(userid, dto.getCourseID());

                if (owned) {
                        Content content = contentRepo.findById(contentId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Content not found with id: " + contentId));

                        mapper.getConfiguration().setSkipNullEnabled(true);
                        mapper.map(dto, content);
                        content.setCourse(courseRepo.findById(dto.getCourseID())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Course not found with id: " + dto.getCourseID())));
                        content = contentRepo.save(content);

                        return mapper.map(content, ContentDTO.class);
                } else
                        throw new RoleMismatchException("Only Teacher can update content");
        }

        @Override
        public ContentDTO getContentById(Long contentId) {
                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));

                ContentDTO dto = mapper.map(content, ContentDTO.class);
                dto.setCourseID(content.getCourse().getCourseid());

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
                                .map(c -> mapper.map(c, ContentDTO.class))
                                .toList();
        }

        @Override
        public void deleteContent(Long userid, Long contentId) {
                owned = courseService.teacherOwnership(userid, contentId);
                if (owned) {
                        Content content = contentRepo.findById(contentId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Content not found: " + contentId));
                        contentRepo.delete(content);
                } else
                        throw new RoleMismatchException("Only Teacher can delete content");
        }

        @Override
        public ContentDTO setContentImage(Long teacherId, Long contentId, Long storedFileId) {

                Content content = contentRepo.findById(contentId)
                                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));

                Long courseId = content.getCourse().getCourseid();

                if (!courseService.teacherOwnership(teacherId, courseId)) {
                        throw new RoleMismatchException("Only course owner can set content image");
                }

                StoredFile file = storedFileRepo.findById(storedFileId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "StoredFile not found: " + storedFileId));

                // must be from same course
                if (!file.getContent().getContentID().equals(content.getContentID())) {
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
                ContentDTO dto = mapper.map(content, ContentDTO.class);

                // manually set ids + signed URL
                dto.setCourseID(courseId);
                dto.setImageFileId(file.getId());
                dto.setImageSignedUrl(storedFileService.getSignedDownloadUrl(file.getId(), 600).getSignedDownloadUrl());

                return dto;
        }
}
