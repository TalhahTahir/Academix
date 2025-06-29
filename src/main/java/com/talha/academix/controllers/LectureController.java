package com.talha.academix.controllers;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.services.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @PostMapping
    public LectureDTO addLecture(@RequestBody LectureDTO dto) {
        return lectureService.addLecture(dto);
    }

    @PutMapping("/{lectureId}")
    public LectureDTO updateLecture(@PathVariable Long lectureId, @RequestBody LectureDTO dto) {
        return lectureService.updateLecture(lectureId, dto);
    }

    @GetMapping("/{lectureId}")
    public LectureDTO getLectureById(@PathVariable Long lectureId) {
        return lectureService.getLectureById(lectureId);
    }

    @GetMapping("/content/{contentId}")
    public List<LectureDTO> getLecturesByContent(@PathVariable Long contentId) {
        return lectureService.getLecturesByContent(contentId);
    }

    @DeleteMapping("/{lectureId}")
    public void deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
    }
}
