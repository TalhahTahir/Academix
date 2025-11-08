package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;

public interface EnrollmentRepo extends JpaRepository<Enrollment, Long> {
        Optional<Enrollment> findByStudentAndCourse(User student, Course course);

        List<Enrollment> findByStudent(User student);

        List<Enrollment> findByCourse(Course course);

        List<Enrollment> findByStatus(EnrollmentStatus status);

        Enrollment findByStudent_UseridAndCourse_Courseid(Long userid, Long courseid);

        long countByCompletionPercentageGreaterThan(int i);

        boolean existsByStudent_UseridAndCourse_Courseid(Long studentId, Long courseId);

        @Query("select e.status from Enrollment e " +
                        "where e.student.userid = :studentId and e.course.courseid = :courseId")
        Optional<EnrollmentStatus> findEnrollmentStatusByStudent_UseridAndCourse_Courseid(
                        @Param("studentId") Long studentId, @Param("courseId") Long courseId);

        long count();

        long countByCourse_Courseid(Long courseId);

        long countByStudent_Userid(Long studentId);

}
