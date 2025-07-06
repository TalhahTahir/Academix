package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.CourseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final ModelMapper mapper;

    @Override
    public CourseDTO createCourse(CourseDTO dto) {
        Course course = mapper.map(dto, Course.class);
        course = courseRepo.save(course);
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public CourseDTO updateCourse(Long courseId, CourseDTO dto) {
        Course existing = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        existing.setCoursename(dto.getCoursename());
        existing.setDuration(dto.getDuration());
        existing.setFees(dto.getFees());
        existing.setCatagory(dto.getCatagory());
        existing = courseRepo.save(existing);
        return mapper.map(existing, CourseDTO.class);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepo.delete(course);
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

}
