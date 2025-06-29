package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Content;

public interface ContentRepo extends JpaRepository<Content, Long> {

    public List<Content> findByCourseID(Long courseId);
}
