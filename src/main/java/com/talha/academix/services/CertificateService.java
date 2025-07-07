package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CertificateDTO;

public interface CertificateService {
    CertificateDTO awardCertificate(Long enrollmentId);
    List<CertificateDTO> getCertificatesByStudent(Long studentId);
    CertificateDTO getCertificateById(Long certificateId);
}