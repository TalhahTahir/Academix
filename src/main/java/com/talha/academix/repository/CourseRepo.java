package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {

    public CourseDTO findCourseByCoursename(String coursename);

    public List<Course> findAllByCategory(CourseCategory category);

    public CourseDTO findByCourseIdAndTeacherId(Long userid, Long courseId);

    public boolean existsByCoursenameAndTeacherId(String coursename, Long teacherid);

    public List<Course> findAllByTeacherId(Long teacherId);

    public List<Course> findAllByState(CourseState state);
}
