package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCatagory;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepo courseRepo;
    private final ModelMapper mapper;
    private final UserService userService;

    @Override
    public CourseDTO createCourse(CourseDTO dto) {
        if (courseRepo.findCourseByCoursename(dto.getCoursename()) == null) {
            Course course = mapper.map(dto, Course.class);
            course = courseRepo.save(course);
            return mapper.map(course, CourseDTO.class);
        } else
            throw new IllegalArgumentException("Already have course: " + dto.getCoursename());

    }

    @Override
    public CourseDTO createCourseByAdmin(Long userid, CourseDTO dto) {
        if (userService.adminValidation(userid)) {
            return createCourse(dto);
        } else
            throw new RoleMismatchException("Only Admin can create course");
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapper.map(course, CourseDTO.class);
    }

    @Override
    public List<CourseDTO> getCourseByCatagory(CourseCatagory catagory) {
        List<Course> courses = courseRepo.findAllByCatagory(catagory);
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        return courses.stream()
                .map(course -> mapper.map(course, CourseDTO.class))
                .toList();
    }

    @Override
    public CourseDTO updateCourse(Long courseId, CourseDTO dto) {
        Course existing = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        existing.setCoursename(dto.getCoursename());
        existing.setDuration(dto.getDuration());
        existing.setFees(dto.getFees());
        existing.setSalary(dto.getSalary());
        existing.setCatagory(dto.getCatagory());
        existing = courseRepo.save(existing);
        return mapper.map(existing, CourseDTO.class);
    }

    @Override
    public CourseDTO updateCourseByAdmin(Long userid, Long courseId, CourseDTO dto) {
        if (userService.adminValidation(userid)) {
            return updateCourse(courseId, dto);
        } else
            throw new RoleMismatchException("Only Admin can update course");
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepo.delete(course);
    }

    @Override
    public void deleteCourseByAdmin(Long userid, Long courseId) {
        if (userService.adminValidation(userid)) {
            deleteCourse(courseId);
        } else
            throw new RoleMismatchException("Only Admin can delete course");
    }

    @Override
    public boolean teacherValidation(Long userid, Long courseId) {
        if (courseRepo.findByCourseIdAndTeacherId(userid, courseId) != null) {
            return true;
        } else
            return false;
    }
}
