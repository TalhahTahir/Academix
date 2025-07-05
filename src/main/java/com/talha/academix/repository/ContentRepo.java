package com.talha.academix.repository;

import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepo extends JpaRepository<Content, Long> {
    List<Content> findByCourse(Course course);
}
