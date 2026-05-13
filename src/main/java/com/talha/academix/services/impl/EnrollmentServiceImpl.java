// EnrollmentServiceImpl.java
package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.exception.AlreadyEnrolledException;
import com.talha.academix.exception.PolicyViolationException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.EnrollmentMapper;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.EnrollmentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepo enrollmentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        return finalizeEnrollment(studentId, courseId);
    }

    @Override
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        return enrollmentMapper.toDto(enrollment);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        List<Enrollment> list = enrollmentRepo.findByStudent(student);
        return list.stream()
                .map(e -> enrollmentMapper.toDto(e))
                .toList();
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<Enrollment> list = enrollmentRepo.findByCourse(course);
        return list.stream()
                .map(e -> enrollmentMapper.toDto(e))
                .toList();
    }

    @Override
    public void withdrawEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        enrollmentRepo.delete(enrollment);
    }

    @Override
    public EnrollmentDTO enrollmentValidation(Long courseId, Long userId) {
        Enrollment enrollment = enrollmentRepo.findByStudent_UseridAndCourse_CourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student and course."));
        return enrollmentMapper.toDto(enrollment);
    }

    @Override
    public EnrollmentDTO updateEnrollment(Long enrollmentId, EnrollmentDTO enrollmentDTO) {
        // 1. Fetch existing enrollment
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        // 2. Update associations if IDs provided
        if (enrollmentDTO.getStudentId() != null) {
            User student = userRepo.findById(enrollmentDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            enrollment.setStudent(student);
        }

        if (enrollmentDTO.getCourseId() != null) {
            Course course = courseRepo.findById(enrollmentDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            enrollment.setCourse(course);
        }

        // 3. Update simple fields
        enrollment.setEnrollmentDate(enrollmentDTO.getEnrollmentDate());

        enrollment.setCompletionPercentage(enrollmentDTO.getCompletionPercentage());
        enrollment.setMarks(enrollmentDTO.getMarks());

        if (enrollmentDTO.getStatus() != null) {
            enrollment.setStatus((enrollmentDTO.getStatus()));
        }

        // 4. Save back to DB
        Enrollment updated = enrollmentRepo.save(enrollment);

        // 5. Map back to DTO (manual for now, or use MapStruct)
        return enrollmentMapper.toDto(updated);
    }

    @Override
    @Transactional
    public EnrollmentDTO finalizeEnrollment(Long studentId, Long courseId) {
        // 1. Prevent double‑enroll
        if (enrollmentRepo.existsByStudent_UseridAndCourse_CourseId(studentId, courseId)) {
            throw new AlreadyEnrolledException(
                    "Student " + studentId + " already enrolled in course " + courseId);
        }

        // 2. Load required entities
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        // 3. Build and save the Enrollment
        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setEnrollmentDate(new Date().toInstant());
        e.setStatus(EnrollmentStatus.IN_PROGRESS);
        e.setCompletionPercentage(0.0);
        e.setMarks(0.0);
        Enrollment saved = enrollmentRepo.save(e);

        // 5. Map to DTO and return
        return enrollmentMapper.toDto(saved);
    }

    @Override
    public Enrollment getEnrollmentEntity(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        return enrollment;
    }

    @Override
    public void updateCompletionPercentage(Long enrollmentId, double percentage) {
        Enrollment enrollment = getEnrollmentEntity(enrollmentId);
        enrollment.setCompletionPercentage(percentage);
        enrollmentRepo.save(enrollment);
    }

    @Override
    public Long countAllEnrollments() {
        return enrollmentRepo.count();
    }

    @Override
    public Long countEnrollmentsByCourse(Long courseId) {
        return enrollmentRepo.countByCourse_CourseId(courseId);
    }

    @Override
    public Long countEnrollmentsByStudent(Long studentId) {
        return enrollmentRepo.countByStudent_Userid(studentId);
    }

    @Override
    public EnrollmentDTO enrollmentCompletion(Long enrollmentId) {

        Enrollment e = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        if ((e.getMarks() > 50) || (e.getCompletionPercentage() == 100)) {
            e.setStatus(EnrollmentStatus.COMPLETED);
        Enrollment enrollment = enrollmentRepo.save(e);
        return enrollmentMapper.toDto(enrollment);
        }
        else
            throw new PolicyViolationException("Enrollment is not completed");
    }

}
