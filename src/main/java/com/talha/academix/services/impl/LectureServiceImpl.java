package com.talha.academix.services.impl;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Lecture;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.services.LectureService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

    private final LectureRepo lectureRepo;
    private final ModelMapper modelMapper;

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
}
