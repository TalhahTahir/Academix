package com.talha.academix.services.impl;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Certificate;
import com.talha.academix.repository.CertificateRepo;
import com.talha.academix.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepo certificateRepo;
    private final ModelMapper modelMapper;

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
        existing.setGrade(dto.getGrade());
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
}
