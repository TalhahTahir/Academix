package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Certificate;
import com.talha.academix.model.Course;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {

    public List<Certificate> findByStudentID(Long studentId);
    public List<Certificate> findByCourseID(Long courseId);

}
