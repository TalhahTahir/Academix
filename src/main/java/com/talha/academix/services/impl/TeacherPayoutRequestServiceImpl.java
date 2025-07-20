package com.talha.academix.services.impl;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.TeacherPayoutRequestDTO;
import com.talha.academix.enums.RequestStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.TeacherPayoutRequest;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.TeacherPayoutRequestRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.PaymentService;
import com.talha.academix.services.TeacherPayoutRequestService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherPayoutRequestServiceImpl implements TeacherPayoutRequestService {

    private final TeacherPayoutRequestRepo payoutRequestRepo;
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final PaymentService paymentService;
    private final ModelMapper modelMapper;

    @Override
    public TeacherPayoutRequestDTO requestPayout(Long teacherId, Long courseId) {
        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherId));
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (!course.getTeacher().getUserid().equals(teacherId)) {
            throw new RoleMismatchException("Teacher does not own this course.");
        }

        // Check content & lecture criteria here if needed
        // e.g. if (course.getLectures().size() < course.getMinLectures()) throw ...

        TeacherPayoutRequest request = new TeacherPayoutRequest();
        request.setTeacher(teacher);
        request.setCourse(course);
        request.setRequestedAt(Instant.now());
        request.setStatus(RequestStatus.PENDING);

        payoutRequestRepo.save(request);
        return modelMapper.map(request, TeacherPayoutRequestDTO.class);
    }

    @Override
    public List<TeacherPayoutRequestDTO> getAllPendingRequests() {
        return payoutRequestRepo.findByStatus(RequestStatus.PENDING)
            .stream()
            .map(req -> modelMapper.map(req, TeacherPayoutRequestDTO.class))
            .toList();
    }

    @Override
    public List<TeacherPayoutRequestDTO> getRequestsByTeacher(Long teacherId) {
        return payoutRequestRepo.findByTeacherId(teacherId)
            .stream()
            .map(req -> modelMapper.map(req, TeacherPayoutRequestDTO.class))
            .toList();
    }

    @Override
    @Transactional
    public TeacherPayoutRequestDTO approveRequest(Long requestId, String adminRemarks) {
        TeacherPayoutRequest request = payoutRequestRepo.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Payout request not found: " + requestId));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalStateException("Request is already processed.");
        }

        paymentService.processPayment(request.getTeacher().getUserid(), request.getCourse().getCourseid());

        request.setStatus(RequestStatus.APPROVED);
        request.setProcessedAt(Instant.now());
        request.setAdminRemarks(adminRemarks);

        payoutRequestRepo.save(request);
        return modelMapper.map(request, TeacherPayoutRequestDTO.class);
    }

    @Override
    @Transactional
    public TeacherPayoutRequestDTO rejectRequest(Long requestId, String adminRemarks) {
        TeacherPayoutRequest request = payoutRequestRepo.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Payout request not found: " + requestId));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalStateException("Request is already processed.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setProcessedAt(Instant.now());
        request.setAdminRemarks(adminRemarks);

        payoutRequestRepo.save(request);
        return modelMapper.map(request, TeacherPayoutRequestDTO.class);
    }
}

