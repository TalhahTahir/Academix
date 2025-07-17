package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
    Enrollment existsByStudentAndCourse(Long studentid, Long courseid);
    List<Enrollment> findByStudent(User student);
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByStatus(EnrollmentStatus status);
    Enrollment findByStudentIDAndCourseID(Long studentId, Long courseid);

    public long countByCompletionPercentageGreaterThan(int i);
}
