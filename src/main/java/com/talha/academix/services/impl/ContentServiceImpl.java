// ContentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.CourseState;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.ContentService;
import com.talha.academix.services.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

        private final ContentRepo contentRepo;
        private final CourseRepo courseRepo;
        private final CourseService courseService;
        private final ActivityLogService activityLogService;
        private final ModelMapper mapper;
        Boolean owned;

        @Override
        public ContentDTO addContent(Long userid, ContentDTO dto) {

                Course course = courseRepo.findById(dto.getCourseID())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Course not found with id: " + dto.getCourseID()));

                owned = courseService.teacherOwnership(userid, course.getCourseid());

                if (owned && (course.getState() == CourseState.DRAFT || course.getState() == CourseState.IN_DEVELOPMENT
                                || course.getState() == CourseState.REJECTED)) {

                        Content content = new Content();
                        content.setCourse(course);
                        content.setDescription(dto.getDescription());
                        content.setImage(dto.getImage());
                        content = contentRepo.save(content);

                        // after saving content:
                        activityLogService.logAction(
                                        userid,
                                        ActivityAction.CONTENT_UPLOAD,
                                        "Content " + content.getContentID() + " uploaded for Course "
                                                        + content.getCourse().getCoursename());

                        return mapper.map(content, ContentDTO.class);
                } else
                        throw new RoleMismatchException(
                                        "Owner can only add content in DRAFT, IN_DEVELOPMENT or REJECTED courses");
        }

        @Override
        public ContentDTO updateContent(Long userid, Long contentId, ContentDTO dto) {

                owned = courseService.teacherOwnership(userid, contentId);

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

                        // after saving content:
                        activityLogService.logAction(
                                        userid,
                                        ActivityAction.CONTENT_UPLOAD,
                                        "Content " + content.getContentID() + " updated for Course "
                                                        + content.getCourse().getCoursename());

                        return mapper.map(content, ContentDTO.class);
                } else
                        throw new RoleMismatchException("Only Teacher can update content");
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
}
