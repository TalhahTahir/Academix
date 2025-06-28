package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.TeacherQualification;

public interface TeacherQualificationRepo extends JpaRepository<TeacherQualification, Long> {
}
