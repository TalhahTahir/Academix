package com.talha.academix.services.impl;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Lecture;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.LectureService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.talha.academix.repository.ContentRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.ContentService;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepo lectureRepo;
    private final ModelMapper modelMapper;
    private final ContentRepo contentRepo;
    private final ContentService contentService;
    private final ActivityLogService activityLogService;

    @Override
    public LectureDTO addLecture(LectureDTO dto) {
        Lecture lecture = modelMapper.map(dto, Lecture.class);
        lecture = lectureRepo.save(lecture);
        return modelMapper.map(lecture, LectureDTO.class);
    }

    @Override
    public LectureDTO updateLecture(Long lectureId, LectureDTO dto) {
        Lecture existing = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));
        existing.setTitle(dto.getTitle());
        existing.setVideoUrl(dto.getVideoUrl());
        existing.setDuration(dto.getDuration());
        existing.setContentId(dto.getContentId());
        existing = lectureRepo.save(existing);
        return modelMapper.map(existing, LectureDTO.class);
    }

    @Override
    public LectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));
        return modelMapper.map(lecture, LectureDTO.class);
    }

    @Override
    public List<LectureDTO> getLecturesByContent(Long contentId) {
        List<Lecture> lectures = lectureRepo.findByContentID(contentId);
        return lectures.stream()
                .map(l -> modelMapper.map(l, LectureDTO.class))
                .toList();
    }

    @Override
    public void deleteLecture(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found with id: " + lectureId));
        lectureRepo.delete(lecture);
    }

    @Override
    public LectureDTO uploadLeatureByTeacher(Long teacherId, LectureDTO dto) {
        Long contentId = dto.getContentId();
        Long courseId = contentRepo.findCourseIdByContentId(contentId);

        if (contentService.verifyTeacher(teacherId, courseId)) {

            activityLogService.logAction(teacherId,
             ActivityAction.CONTENT_UPLOAD,
             "Upload lecture by teacher " + teacherId + " for course " + courseId);

            return addLecture(dto);

        } else
            throw new ForbiddenException("Teacher is not authorized to upload lecture");
    }

    @Override
    public void deleteLectureByTeacher(Long teacherId, Long lectureId) {
        Long contentId = getLectureById(lectureId).getContentId();
        Long courseId = contentRepo.findCourseIdByContentId(contentId);
        if (contentService.verifyTeacher(teacherId, courseId)) {
            deleteLecture(lectureId);
        } else
            throw new ForbiddenException("Teacher is not authorized to delete lecture");
    }

}
