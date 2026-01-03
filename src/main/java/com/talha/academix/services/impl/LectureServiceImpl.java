// LectureServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.mapper.LectureMapper;
import com.talha.academix.model.Content;
import com.talha.academix.model.Lecture;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.LectureService;
import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.enums.StoredFileType;
import com.talha.academix.model.StoredFile;
import com.talha.academix.repository.StoredFileRepo;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepo lectureRepo;
    private final ContentRepo contentRepo;
    private final CourseService courseService;
    private final StoredFileRepo storedFileRepo;
    private final StoredFileService storedFileService;
    private final LectureMapper lectureMapper;

    @Override
    public LectureDTO addLecture(Long userid, LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (!courseService.teacherOwnership(userid, content.getCourse().getCourseId())) {
            throw new RoleMismatchException("only valid user can add lecture");
        }

        StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

        // verify file belongs to same course
        if (!file.getContent().getContentId().equals(content.getContentId())) {
            throw new RoleMismatchException("StoredFile does not belong to this content");
        }

        // verify correct type and status
        if (file.getType() != StoredFileType.LECTURE) {
            throw new RoleMismatchException("StoredFile type must be LECTURE");
        }
        if (file.getStatus() != StoredFileStatus.READY) {
            throw new RoleMismatchException("StoredFile must be READY before linking");
        }

        Lecture lecture = new Lecture();
        lecture.setContent(content);
        lecture.setTitle(dto.getTitle());
        lecture.setDuration(dto.getDuration());
        lecture.setVideoUrl(file);
        lecture = lectureRepo.save(lecture);

        LectureDTO out = lectureMapper.toDto(lecture);
        out.setVideoSignedUrl(storedFileService.getSignedDownloadUrl(file.getId(), 600).getSignedDownloadUrl());
        return out;
    }

    @Override
    public LectureDTO updateLecture(Long userid, Long lectureId, LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseId())) {

            Lecture existing = lectureRepo.findById(lectureId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

            // if contentId changed, reassign content
            if (!existing.getContent().getContentId().equals(content.getContentId())) {
                Content newcontent = contentRepo.findById(dto.getContentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
                existing.setContent(newcontent);
            }

            existing = lectureRepo.save(existing);

            return lectureMapper.toDto(existing);
        } else
            throw new RoleMismatchException("only teacher can update lectures");
    }

    @Override
    public LectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));
        return lectureMapper.toDto(lecture);
    }

    @Override
    public List<LectureDTO> getLecturesByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return lectureRepo.findByContent(content).stream()
                .map(l -> lectureMapper.toDto(l))
                .toList();
    }

    @Override
    public void deleteLecture(Long userid, Long lectureId) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

        Content content = contentRepo.findById(lecture.getContent().getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("COntent not found"));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseId())) {
            lectureRepo.delete(lecture);
        } else
            throw new RoleMismatchException("Only Teacher can delete lecture");
    }
}
