package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.services.CourseService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.CourseViewDTO;
import com.talha.academix.dto.CreateCourseDTO;
import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public CourseDTO createCourse(@RequestBody CreateCourseDTO dto) {
        return courseService.createCourse(dto);
    }

    @GetMapping("/count")
    public Long countAllLong() {
        return courseService.countAll();
    }
    

    @GetMapping("/{id}")
    public CourseDTO getCourseByID(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/{id}/all")
    public List<CourseViewDTO> viewAllCourses(@PathVariable("id") Long studentId) {
        return courseService.viewAllCourses(studentId);
    }

    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/view/category")
    public List<CourseDTO> getCoursesByCategory(@RequestParam CourseCategory category) {
        return courseService.getCourseByCategory(category);
    }

    @GetMapping("/view/teacher/{teacherId}")
    public List<CourseDTO> getCoursesByTeacher(@PathVariable Long teacherId) {
        return courseService.getAllCoursesByTeacher(teacherId);
    }

    @GetMapping("/view/state")
    public List<CourseDTO> courseByState(@RequestParam CourseState state) {
        return courseService.getAllCoursesByState(state);
    }

    @PostMapping("/state/action/reject/{courseId}")
    public CourseDTO courseRejection(@PathVariable Long courseId) {
        return courseService.courseRejection(courseId);
    }

    @PostMapping("/state/action/approve/{courseId}")
    public CourseDTO courseApproval(@PathVariable Long courseId) {
        return courseService.courseApproval(courseId);
    }

    @PostMapping("/state/action/disable/{courseId}")
    public CourseDTO courseDisable(@PathVariable Long courseId) {
        return courseService.courseDisable(courseId);
    }

    @PostMapping("/state/action/modify/{teacherId}/{courseId}")
    public CourseDTO courseModification(@PathVariable Long teacherId, @PathVariable Long courseId,
            @RequestBody CourseDTO dto) {
        return courseService.courseModification(teacherId, courseId, dto);
    }

    @PostMapping("/state/action/develop/{teacherId}/{courseId}")
    public CourseDTO courseDevelop(@PathVariable Long teacherId, @PathVariable Long courseId) {
        return courseService.courseDevelopment(teacherId, courseId);
    }

    @PostMapping("/state/action/launch/{teacherId}/{courseId}")
    public CourseDTO courseLaunch(@PathVariable Long teacherId, @PathVariable Long courseId) {
        return courseService.courseLaunch(teacherId, courseId);
    }

    @PutMapping("/{id}")
    public CourseDTO updateCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        return courseService.updateCourse(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
