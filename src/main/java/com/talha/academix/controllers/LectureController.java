package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.LectureDTO;
import com.talha.academix.services.LectureService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    @PreAuthorize("@contentSecurity.isContentOwner(principal, #dto.contentId)")
    @PostMapping
    public LectureDTO addLecture(@RequestBody LectureDTO dto) {
        return lectureService.addLecture(dto);
    }

    @PreAuthorize("@lectureSecurity.isLectureOwner(principal, #lectureId)")
    @PutMapping("/{lectureId}")
    public LectureDTO updateLecture(@PathVariable Long lectureId,
            @RequestBody LectureDTO dto) {
        return lectureService.updateLecture(lectureId, dto);
    }

    @GetMapping("{id}")
    public LectureDTO getLectureById(@PathVariable Long id) {
        return lectureService.getLectureById(id);
    }

    @GetMapping("contents/{id}")
    public List<LectureDTO> getLecturesByContent(@PathVariable Long id) {
        return lectureService.getLecturesByContent(id);
    }

    @DeleteMapping("/{lectureId}")
    @PreAuthorize("@lectureSecurity.isLectureOwner(principal, #lectureId)")
    public void deleteLecture(@PathVariable Long lectureId) {
        lectureService.deleteLecture(lectureId);
    }

}