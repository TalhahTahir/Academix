package com.talha.academix.services.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.UncompleteEnrollmentException;
import com.talha.academix.model.Certificate;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.User;
import com.talha.academix.repository.CertificateRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepo certificateRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final UserRepo userRepo;
    private final ActivityLogService activityLogService;
    private final ModelMapper modelMapper;

    @Override
    public CertificateDTO awardCertificate(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));

        if(!enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)){
            throw new UncompleteEnrollmentException("Enrollment status must be COMPLETED to award certificate.");
        }

        Certificate certificate = new Certificate();
        certificate.setStudent(enrollment.getStudent());
        certificate.setTeacher(enrollment.getCourse().getTeacher());
        certificate.setCourse(enrollment.getCourse());
        certificate.setMarks(enrollment.getMarks());
        certificate.setDate(new Date().toInstant());

        certificateRepo.save(certificate);

        activityLogService.logAction(
                enrollment.getStudent().getUserid(),
                ActivityAction.CERTIFICATE_AWARDED,
                "Certificate " + certificate.getCertificateId() + " awarded to Student " + enrollment.getStudent().getUserid() +
                        " for Course " + enrollment.getCourse().getCourseid());

        return modelMapper.map(certificate, CertificateDTO.class);
    }

    @Override
    public List<CertificateDTO> getCertificatesByStudent(Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        return certificateRepo.findByStudent(student).stream()
                .map(c -> modelMapper.map(c, CertificateDTO.class))
                .toList();
    }

    @Override
    public CertificateDTO getCertificateById(Long certificateId) {
        Certificate certificate = certificateRepo.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + certificateId));
        return modelMapper.map(certificate, CertificateDTO.class);
    }
}
