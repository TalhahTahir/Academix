package com.talha.academix.services.impl;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Certificate;
import com.talha.academix.model.Enrollment;
import com.talha.academix.repository.CertificateRepo;
import com.talha.academix.services.CertificateService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.ActivityLogService;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepo certificateRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final ExamRepo examRepo;
    private final ModelMapper modelMapper;
    private final ActivityLogService activityLogService;

    @Override
    public CertificateDTO addCertificate(CertificateDTO dto) {
        Certificate cert = modelMapper.map(dto, Certificate.class);
        cert = certificateRepo.save(cert);
        return modelMapper.map(cert, CertificateDTO.class);
    }

    @Override
    public CertificateDTO updateCertificate(Long certificateId, CertificateDTO dto) {
        Certificate existing = certificateRepo.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + certificateId));
        existing.setStudentId(dto.getStudentId());
        existing.setCourseId(dto.getCourseId());
        existing.setMarks(dto.getMarks());
        existing.setDate(dto.getDate());
        existing.setTeacherId(dto.getTeacherId());
        existing = certificateRepo.save(existing);
        return modelMapper.map(existing, CertificateDTO.class);
    }

    @Override
    public CertificateDTO getCertificateById(Long certificateId) {
        Certificate cert = certificateRepo.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + certificateId));
        return modelMapper.map(cert, CertificateDTO.class);
    }

    @Override
    public List<CertificateDTO> getCertificatesByStudent(Long studentId) {
        List<Certificate> certs = certificateRepo.findByStudentID(studentId);
        return certs.stream()
                .map(c -> modelMapper.map(c, CertificateDTO.class))
                .toList();
    }

    @Override
    public List<CertificateDTO> getCertificatesByCourse(Long courseId) {
        List<Certificate> certs = certificateRepo.findByCourseID(courseId);
        return certs.stream()
        .map(c -> modelMapper.map(c, CertificateDTO.class))
        .toList();
    }

    @Override
    public void deleteCertificate(Long certificateId) {
        Certificate cert = certificateRepo.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id: " + certificateId));
        certificateRepo.delete(cert);
    }

	@Override
	public CertificateDTO awardCertificate( CertificateDTO dto) {
		
        Enrollment enrollment = enrollmentRepo.findByStudentIDAndCourseID(dto.getStudentId(), dto.getCourseId());

        if(enrollment == null) {
            throw new ResourceNotFoundException("Enrollment not found ");
            }

        if(!enrollment.getStatus().equals(EnrollmentStatus.COMPLETED)){
            throw new ResourceNotFoundException("Enrollment not completed");
        }

        // create certificate
    Certificate certificate = new Certificate();
    certificate.setStudentId(dto.getStudentId());
    certificate.setTeacherId(dto.getTeacherId()); 
    certificate.setCourseId(dto.getCourseId());
    certificate.setMarks(enrollment.getMarks());
    certificate.setDate(new Date());

    certificate = certificateRepo.save(certificate);

        // log activity
    activityLogService.logAction(
        certificate.getStudentId(),
        ActivityAction.CERTIFICATE_AWARDED,
        "Student " + certificate.getStudentId() + " enrolled in Course " + certificate.getCourseId()
    );

    return modelMapper.map(certificate, CertificateDTO.class);
        
	}
}
