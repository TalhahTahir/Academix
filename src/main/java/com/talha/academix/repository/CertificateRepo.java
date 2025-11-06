package com.talha.academix.repository;

import com.talha.academix.model.Certificate;
import com.talha.academix.model.User;
import com.talha.academix.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStudent(User student);
    List<Certificate> findByCourse(Course course);
    Optional<Certificate> findByStudentAndCourse(User student, Course course);
    Long countByStudent(User student);
    long count();
    Long countByCourse(Course course);
    
}
