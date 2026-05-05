package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.repository.CourseRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("courseSecurity")
public class CourseSecurity {

    private final CourseRepo courseRepo;

    public boolean isCourseOwner(CustomUserDetails principal, Long courseId) {
        if (principal == null) {
            return false;
        }

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return course.getTeacher().getUserid().equals(principal.getId());
    }
}
