// EnrollmentServiceImpl.java
package com.talha.academix.services.impl;

import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.exception.AlreadyEnrolledException;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepo enrollmentRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final PaymentService paymentService;
    private final ActivityLogService activityLogService;
    private final ModelMapper mapper;

    @Override
    @Transactional
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        // 1. Charge the student (or trigger transfer for teacher)
        PaymentDTO payment = paymentService.processPayment(studentId, courseId);

        // 2. If payment requires action, bubble a special exception
        if (Boolean.TRUE.equals(payment.getRequiresAction())) {
            throw new PaymentFailedException(
                    "Further authentication required",
                    payment.getClientSecret());
        }

        // 3. If payment succeeded, create the enrollment
        return finalizeEnrollment(studentId, courseId);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        List<Enrollment> list = enrollmentRepo.findByStudent(student);
        return list.stream()
                .map(e -> mapper.map(e, EnrollmentDTO.class))
                .toList();
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        List<Enrollment> list = enrollmentRepo.findByCourse(course);
        return list.stream()
                .map(e -> mapper.map(e, EnrollmentDTO.class))
                .toList();
    }

    @Override
    public void withdrawEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        enrollmentRepo.delete(enrollment);
    }

    @Override
    public EnrollmentDTO enrollmentValidation(Long courseid, Long userid) {
        Enrollment enrollment = enrollmentRepo.findByStudentIDAndCourseID(userid, courseid);
        return mapper.map(enrollment, EnrollmentDTO.class);
    }

    @Override
    public EnrollmentDTO updateEnrollment(EnrollmentDTO enrollmentDTO) {
        // 1. Fetch existing enrollment
        Enrollment enrollment = enrollmentRepo.findById(enrollmentDTO.getEnrollmentID())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        // 2. Update associations if IDs provided
        if (enrollmentDTO.getStudentID() != null) {
            User student = userRepo.findById(enrollmentDTO.getStudentID())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            enrollment.setStudent(student);
        }

        if (enrollmentDTO.getCourseID() != null) {
            Course course = courseRepo.findById(enrollmentDTO.getCourseID())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            enrollment.setCourse(course);
        }

        // 3. Update simple fields
        enrollment.setEnrollmentDate(enrollmentDTO.getEnrollmentDate().toInstant());

        enrollment.setCompletionPercentage(enrollmentDTO.getCompletionPercentage());
        enrollment.setMarks(enrollmentDTO.getMarks());

        if (enrollmentDTO.getStatus() != null) {
            enrollment.setStatus((enrollmentDTO.getStatus()));
        }

        // 4. Save back to DB
        Enrollment updated = enrollmentRepo.save(enrollment);

        // 5. Map back to DTO (manual for now, or use MapStruct)
        EnrollmentDTO updatedDTO = new EnrollmentDTO();
        updatedDTO.setEnrollmentID(updated.getEnrollmentID());
        updatedDTO.setStudentID(updated.getStudent().getUserid());
        updatedDTO.setCourseID(updated.getCourse().getCourseid());
        updatedDTO.setEnrollmentDate(
                updated.getEnrollmentDate().atZone(ZoneId.systemDefault()));

        updatedDTO.setStatus(updated.getStatus());
        updatedDTO.setCompletionPercentage(updated.getCompletionPercentage());
        updatedDTO.setMarks(updated.getMarks());

        return updatedDTO;
    }

    @Override
    @Transactional
    public EnrollmentDTO finalizeEnrollment(Long studentId, Long courseId) {
        // 1. Prevent double‑enroll
        if (enrollmentRepo.existsByStudentIdAndCourseId(studentId, courseId)) {
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
        e.setCompletionPercentage(0);
        Enrollment saved = enrollmentRepo.save(e);

        // 4. Log the action
        activityLogService.logAction(
                studentId,
                ActivityAction.ENROLLMENT,
                "Student " + studentId + " enrolled in Course " + courseId);

        // 5. Map to DTO and return
        return mapper.map(saved, EnrollmentDTO.class);
    }

}
