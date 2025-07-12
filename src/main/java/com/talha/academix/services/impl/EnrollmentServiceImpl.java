// EnrollmentServiceImpl.java
package com.talha.academix.services.impl;

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
        User student = userRepo.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (enrollmentValidation(student.getUserid(), course.getCourseid()) != null) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        PaymentDTO paid = paymentService.processPayment(studentId, courseId);
        if (paid == null) {
            throw new PaymentFailedException("Payment failed");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(new Date());
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setCompletionPercentage(0);
        enrollment = enrollmentRepo.save(enrollment);

        activityLogService.logAction(
            studentId, ActivityAction.ENROLLMENT,
            "Student " + studentId + " enrolled in Course " + courseId
        );

        return mapper.map(enrollment, EnrollmentDTO.class);
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
public EnrollmentDTO enrollmentValidation(Long courseid, Long userid){
    Enrollment enrollment = enrollmentRepo.existsByStudentAndCourse(userid, courseid);
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
    enrollment.setEnrollmentDate(enrollmentDTO.getEnrollmentDate());
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
    updatedDTO.setEnrollmentDate(updated.getEnrollmentDate());
    updatedDTO.setStatus(updated.getStatus());
    updatedDTO.setCompletionPercentage(updated.getCompletionPercentage());
    updatedDTO.setMarks(updated.getMarks());

    return updatedDTO;
}


}
