package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Lecture;
import com.talha.academix.repository.LectureRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("lectureSecurity")
public class LectureSecurity {
    
    private final LectureRepo lectureRepo;

    public boolean isLectureOwner(CustomUserDetails principal, Long lectureId) {
        if (principal == null || lectureId == null) {
            return false;
        }

        Lecture lecture = lectureRepo.findById(lectureId)
                .orElseThrow(() -> new ResourceNotFoundException("lecture not found with id: " + lectureId));

        return lecture.getContent().getCourse().getTeacher().getUserid().equals(principal.getId());

    }
}
