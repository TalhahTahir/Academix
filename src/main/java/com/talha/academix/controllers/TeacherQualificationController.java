package com.talha.academix.controllers;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.services.TeacherQualificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.talha.academix.enums.Degree;

@RestController
@RequestMapping("/api/qualifications")
@RequiredArgsConstructor
public class TeacherQualificationController {

    private final TeacherQualificationService qualificationService;

    @PostMapping
    public TeacherQualificationDTO addQualification(@RequestBody TeacherQualificationDTO dto) {
        return qualificationService.addQualification(dto);
    }

    @PutMapping("/{qualificationId}")
    public TeacherQualificationDTO updateQualification(@PathVariable Long qualificationId, @RequestBody TeacherQualificationDTO dto) {
        return qualificationService.updateQualification(qualificationId, dto);
    }

    @GetMapping("/{qualificationId}")
    public TeacherQualificationDTO getQualificationById(@PathVariable Long qualificationId) {
        return qualificationService.getQualificationById(qualificationId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<TeacherQualificationDTO> getQualificationsByTeacher(@PathVariable Long teacherId) {
        return qualificationService.getQualificationsByTeacher(teacherId);
    }

    @GetMapping("/degree/{degree}")
    public List<TeacherQualificationDTO> getQualificationsByDegree(@PathVariable Degree degree) {
        return qualificationService.getQualificationsByDegree(degree);
    }

    @DeleteMapping("/{qualificationId}")
    public void deleteQualification(@PathVariable Long qualificationId) {
        qualificationService.deleteQualification(qualificationId);
    }
}
