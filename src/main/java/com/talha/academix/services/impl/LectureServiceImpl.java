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
    private final StoredFileRepo storedFileRepo;
    private final StoredFileService storedFileService;
    private final LectureMapper lectureMapper;

    @Override
    public LectureDTO addLecture(LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

        validateStoredFile(file, content);

        Lecture lecture = new Lecture();
        lecture.setContent(content);
        lecture.setVideoUrl(file);
        lectureMapper.updateEntityFromDto(dto, lecture);
        lecture = lectureRepo.save(lecture);

        return enrichWithSignedUrl(lecture);
    }

    @Override
    public LectureDTO updateLecture(Long lectureId, LectureDTO dto) {

        Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        Lecture existing = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

        // if contentId changed, reassign content
        if (!existing.getContent().getContentId().equals(content.getContentId())) {
            existing.setContent(content);
        }

        lectureMapper.updateEntityFromDto(dto, existing);

        // if storedFileId changed, reassign File
        if (dto.getStoredFileId() != null && 
            (existing.getVideoUrl() == null || !existing.getVideoUrl().getId().equals(dto.getStoredFileId()))) {
            
            StoredFile file = storedFileRepo.findById(dto.getStoredFileId())
                    .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + dto.getStoredFileId()));

            validateStoredFile(file, content);
            existing.setVideoUrl(file);
        }

        existing = lectureRepo.save(existing);
        
        return enrichWithSignedUrl(existing);
    }

    @Override
    public LectureDTO getLectureById(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));
        return enrichWithSignedUrl(lecture);
    }

    @Override
    public List<LectureDTO> getLecturesByContent(Long contentId) {
        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + contentId));
        return lectureRepo.findByContent(content).stream()
                .map(this::enrichWithSignedUrl)
                .toList();
    }

    @Override
    public void deleteLecture(Long lectureId) {

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

        lectureRepo.delete(lecture);
    }

    private void validateStoredFile(StoredFile file, Content content) {
        if (!file.getContent().getContentId().equals(content.getContentId())) {
            throw new RoleMismatchException("StoredFile does not belong to this content");
        }
        if (file.getType() != StoredFileType.LECTURE) {
            throw new RoleMismatchException("StoredFile type must be LECTURE");
        }
        if (file.getStatus() != StoredFileStatus.READY) {
            throw new RoleMismatchException("StoredFile must be READY before linking");
        }
    }

    private LectureDTO enrichWithSignedUrl(Lecture lecture) {
        LectureDTO dto = lectureMapper.toDto(lecture);
        if (lecture.getVideoUrl() != null) {
            dto.setVideoSignedUrl(storedFileService.getSignedDownloadUrl(lecture.getVideoUrl().getId(), 600).getSignedDownloadUrl());
        }
        return dto;
    }
}
