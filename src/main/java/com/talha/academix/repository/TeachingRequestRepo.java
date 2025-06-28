package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.TeachingRequest;

public interface TeachingRequestRepo extends JpaRepository<TeachingRequest, Long> {
}
