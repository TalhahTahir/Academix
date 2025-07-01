package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.TeachingRequest;

public interface TeachingRequestRepo extends JpaRepository<TeachingRequest, Long> {

    public List<TeachingRequest> findByTeacherId(Long teacherId);

    public List<TeachingRequest> findByCourseId(Long courseId);
}
