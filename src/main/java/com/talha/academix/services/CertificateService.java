package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CertificateDTO;

public interface CertificateService {
    CertificateDTO awardCertificate(Long enrollmentId);
    List<CertificateDTO> getAllByStudent(Long studentId);
    CertificateDTO getById(Long certificateId);
    Long countAll();
    Long countAllByStudent(Long studentId);
    Long countAllByCourse(Long courseId);

}