// LectureServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
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
    private final ModelMapper mapper;

    @Override
    public LectureDTO addLecture(Long userid, LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (!courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {
            throw new RoleMismatchException("only valid user can add lecture");
        }

        StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

        // verify file belongs to same course
        if (!file.getContent().getContentID().equals(content.getContentID())) {
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

        // build response DTO manually (avoid ModelMapper lazy issues)
        LectureDTO out = new LectureDTO();
        out.setLectureId(lecture.getLectureId());
        out.setContentId(content.getContentID());
        out.setTitle(lecture.getTitle());
        out.setDuration(lecture.getDuration());
        out.setStoredFileId(file.getId());
        out.setVideoSignedUrl(storedFileService.getSignedDownloadUrl(file.getId(), 600).getSignedDownloadUrl());
        return out;
    }

    @Override
    public LectureDTO updateLecture(Long userid, Long lectureId, LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {

            Lecture existing = lectureRepo.findById(lectureId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

            mapper.getConfiguration().setSkipNullEnabled(true);
            mapper.map(dto, existing);

            // if contentId changed, reassign content
            if (!existing.getContent().getContentID().equals(content.getContentID())) {
                Content newcontent = contentRepo.findById(dto.getContentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
                existing.setContent(newcontent);
            }

            existing = lectureRepo.save(existing);

            return mapper.map(existing, LectureDTO.class);
        } else
            throw new RoleMismatchException("only teacher can update lectures");
    }

    @Override
    public LectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));
        return mapper.map(lecture, LectureDTO.class);
    }

    @Override
    public List<LectureDTO> getLecturesByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return lectureRepo.findByContent(content).stream()
                .map(l -> mapper.map(l, LectureDTO.class))
                .toList();
    }

    @Override
    public void deleteLecture(Long userid, Long lectureId) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

        Content content = contentRepo.findById(lecture.getContent().getContentID())
                .orElseThrow(() -> new ResourceNotFoundException("COntent not found"));

        if (courseService.teacherOwnership(userid, content.getCourse().getCourseid())) {
            lectureRepo.delete(lecture);
        } else
            throw new RoleMismatchException("Only Teacher can delete lecture");
    }
}
