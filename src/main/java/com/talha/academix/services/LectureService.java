package com.talha.academix.services;

import com.talha.academix.dto.LectureDTO;
import java.util.List;

public interface LectureService {
    LectureDTO addLecture(LectureDTO dto);
    LectureDTO updateLecture(Long lectureId, LectureDTO dto);
    LectureDTO getLectureById(Long lectureId);
    List<LectureDTO> getLecturesByContent(Long contentId);
    void deleteLecture(Long lectureId);
}
