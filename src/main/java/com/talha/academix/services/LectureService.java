package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.LectureDTO;

public interface LectureService {
    LectureDTO addLecture(Long userid, LectureDTO dto);
    LectureDTO updateLecture(Long userid, Long lectureId, LectureDTO dto);
    LectureDTO getLectureById(Long lectureId);
    List<LectureDTO> getLecturesByContent(Long contentId);
    void deleteLecture(Long userid, Long lectureId);
}