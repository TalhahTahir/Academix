package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.exception.AlreadyEnrolledException;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Wallet;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.DocumentRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.LectureRepo;
import com.talha.academix.repository.StudentContentProgressRepo;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.EnrollmentServices;
import com.talha.academix.services.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentServices {

    private final EnrollmentRepo enrollmentRepo;
    private final ModelMapper modelMapper;
    private final CourseRepo courseRepo;
    private final WalletRepo walletRepo;
    private final PaymentService paymentService;

    private final LectureRepo lectureRepo;
    private final DocumentRepo documentRepo;
    private final StudentContentProgressRepo progressRepo;

    private final ActivityLogService activityLogService;

    // business Logics

    @Override
    public EnrollmentDTO enrollStudentInCourse(Long studentId, Long courseId) {

        if (enrollmentRepo.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AlreadyEnrolledException("Student is already enrolled in this course");
        }

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Wallet wallet = walletRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        boolean paymentSuccess = paymentService.processPayment(studentId, course, wallet);
        if (!paymentSuccess) {  
            throw new PaymentFailedException("Payment failed");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentID(studentId);
        enrollment.setCourseID(courseId);
        enrollment.setEnrollmentDate(new Date());
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollment.setCompletionPercentage(0);
        enrollmentRepo.save(enrollment);

        // log activity
        activityLogService.logAction(
                studentId,
                ActivityAction.ENROLLMENT,
                "Student " + studentId + " enrolled in Course " + courseId);

        return modelMapper.map(enrollment, EnrollmentDTO.class);

    }

    public void notifyStudentEnrollment() {

    }

    // Simple CRUD for testing or ADMIN use
    @Override
    public EnrollmentDTO enrollStudent(EnrollmentDTO dto) {
        Enrollment enrollment = modelMapper.map(dto, Enrollment.class);
        enrollment = enrollmentRepo.save(enrollment);
        return modelMapper.map(enrollment, EnrollmentDTO.class);
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(Long studentId) {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);
        return enrollments.stream()
                .map(e -> modelMapper.map(e, EnrollmentDTO.class))
                .toList();
    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepo.findByCourseId(courseId);
        return enrollments.stream()
                .map(e -> modelMapper.map(e, EnrollmentDTO.class))
                .toList();
    }

    @Override
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = enrollmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
        enrollmentRepo.delete(enrollment);
    }

    @Transactional
    @Override
    public boolean updateCourseCompletionPercentage(Long studentId, Long courseId) {

        // 1️⃣ Find enrollment
        Enrollment enrollment = enrollmentRepo.findByStudentIDAndCourseID(studentId, courseId);
        if (enrollment == null) {
            throw new ResourceNotFoundException(
                    "Enrollment not found for studentId: " + studentId + " and courseId: " + courseId);
        }

        // 2️⃣ Get total content items (videos + documents)
        int totalLectures = lectureRepo.countByCourseId(courseId);
        int totalDocuments = documentRepo.countByCourseId(courseId);
        int totalItems = totalLectures + totalDocuments;

        if (totalItems == 0) {
            throw new IllegalStateException("Course has no content items to complete.");
        }

        // 3️⃣ Get completed items by this student
        int completedItems = progressRepo.countCompletedByStudentAndCourse(studentId, courseId);

        // 4️⃣ Calculate percentage
        double percentage = ((double) completedItems / totalItems) * 100;
        enrollment.setCompletionPercentage(percentage);

        // 5️⃣ Save update
        enrollmentRepo.save(enrollment);

        return true;
    }

    @Override
    public boolean courseCompletion(Long enrollmentId) {

        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (enrollment.getCompletionPercentage() < 100) {
            throw new ResourceNotFoundException("Complete 100% of the course to get the certificate");
        }

        if (enrollment.getMarks() < 50) {
            throw new ResourceNotFoundException("Pass the Exam to get the certificate");
        }

        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollmentRepo.save(enrollment);

        return true;

    }

}