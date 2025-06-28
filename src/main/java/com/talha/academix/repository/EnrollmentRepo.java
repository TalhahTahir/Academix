package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Enrollment;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long>{
    
}
