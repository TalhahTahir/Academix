package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;
import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {

    Course findCourseByCoursename(String coursename);

    List<Course> findAllByCategory(CourseCategory category);

    Course findByCourseIdAndTeacherId(Long userid, Long courseId);

    boolean existsByCoursenameAndTeacherId(String coursename, Long teacherid);

    List<Course> findAllByTeacherId(Long teacherId);

    List<Course> findAllByState(CourseState state);
}
