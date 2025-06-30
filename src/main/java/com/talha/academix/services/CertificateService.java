package com.talha.academix.services;

import com.talha.academix.dto.CertificateDTO;
import java.util.List;

public interface CertificateService {
    CertificateDTO addCertificate(CertificateDTO dto);
    CertificateDTO updateCertificate(Long certificateId, CertificateDTO dto);
    CertificateDTO getCertificateById(Long certificateId);
    List<CertificateDTO> getCertificatesByStudent(Long studentId);
    List<CertificateDTO> getCertificatesByCourse(Long courseId);
    void deleteCertificate(Long certificateId);
}
