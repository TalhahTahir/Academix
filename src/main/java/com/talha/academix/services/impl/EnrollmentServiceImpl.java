package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.enums.PaymentType;
import com.talha.academix.exception.AlreadyEnrolledException;
import com.talha.academix.exception.PaymentFailedException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Payment;
import com.talha.academix.model.Wallet;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.PaymentRepo;
import com.talha.academix.repository.WalletRepo;
import com.talha.academix.services.EnrollmentServices;
import com.talha.academix.services.PaymentGatewayService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentServices {

    private final EnrollmentRepo enrollmentRepo;
    private final ModelMapper modelMapper;
    private final CourseRepo courseRepo;
    private final WalletRepo walletRepo;
    private final PaymentRepo paymentRepo;
    private final PaymentGatewayService paymentGatewayService;

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

        boolean paymentSuccess = paymentGatewayService.charge(
                wallet.getMedium(),
                wallet.getAccount(),
                course.getFees());

        if (!paymentSuccess) {
            throw new PaymentFailedException("Payment failed or declined!");
        }

        Payment payment = new Payment();
        payment.setUserID(studentId);
        payment.setCourseID(courseId);
        payment.setAmount(course.getFees());
        payment.setMedium(wallet.getMedium());
        payment.setAccount(wallet.getAccount());
        payment.setPaymentType(PaymentType.INCOMING);
        payment.setDate(new Date());
        paymentRepo.save(payment);

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentID(studentId);
        enrollment.setCourseID(courseId);
        enrollment.setEnrollmentDate(new Date());
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        enrollmentRepo.save(enrollment);

        return modelMapper.map(enrollment, EnrollmentDTO.class);

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

}
