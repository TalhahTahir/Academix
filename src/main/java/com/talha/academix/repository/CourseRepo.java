package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.enums.CourseCatagory;
import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long> {

    public CourseDTO findCourseByCoursename(String coursename);

    public List<Course> findAllByCatagory(CourseCatagory catagory);

    public CourseDTO findByCourseIdAndTeacherId(Long userid, Long courseId);

    public Object findCourseByCoursenameAndTeacherid(String coursename, Long teacherid);
}
