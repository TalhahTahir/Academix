package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Enrollment;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.services.EnrollmentServices;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentServices {
    private final EnrollmentRepo enrollmentRepo;
    private final ModelMapper modelMapper;
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
