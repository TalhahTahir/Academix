package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Course;

public interface CourseRepo extends JpaRepository<Course, Long>{

    public Course findByContentID(Long contentId);
    
}
