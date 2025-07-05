package com.talha.academix.repository;

import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;
import com.talha.academix.model.Course;
import com.talha.academix.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    boolean existsByStudentAndCourse(User student, Course course);
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByStatus(EnrollmentStatus status);
}
