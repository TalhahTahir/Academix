// LectureServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Lecture;
import com.talha.academix.repository.ContentRepo;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.LectureService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepo lectureRepo;
    private final ContentRepo contentRepo;
    private final ModelMapper mapper;

    @Override
    public LectureDTO addLecture(LectureDTO dto) {
        Content content = contentRepo.findById(dto.getContentId())
            .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));

        Lecture lecture = new Lecture();
        lecture.setContent(content);
        lecture.setTitle(dto.getTitle());
        lecture.setVideoUrl(dto.getVideoUrl());
        lecture.setDuration(dto.getDuration());
        lecture = lectureRepo.save(lecture);

        return mapper.map(lecture, LectureDTO.class);
    }

    @Override
    public LectureDTO updateLecture(Long lectureId, LectureDTO dto) {
        Lecture existing = lectureRepo.findById(lectureId)
            .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));

        // if contentId changed, reassign content
        if (!existing.getContent().getContentID().equals(dto.getContentId())) {
            Content content = contentRepo.findById(dto.getContentId())
                .orElseThrow(() -> new ResourceNotFoundException("Content not found: " + dto.getContentId()));
            existing.setContent(content);
        }
        existing.setTitle(dto.getTitle());
        existing.setVideoUrl(dto.getVideoUrl());
        existing.setDuration(dto.getDuration());
        existing = lectureRepo.save(existing);

        return mapper.map(existing, LectureDTO.class);
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
    public void deleteLecture(Long lectureId) {
        Lecture lecture = lectureRepo.findById(lectureId)
            .orElseThrow(() -> new ResourceNotFoundException("Lecture not found: " + lectureId));
        lectureRepo.delete(lecture);
    }
}
