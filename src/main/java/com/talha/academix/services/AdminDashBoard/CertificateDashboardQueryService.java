package com.talha.academix.services.admindashboard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashboardDTO;
import com.talha.academix.repository.CertificateRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateDashboardQueryService {
    
    private final CertificateRepo certificateRepo;

    public AdminDashboardDTO.Certificates certificatesSection(){
        return AdminDashboardDTO.Certificates.builder()
                .totalCertificates(certificateRepo.count())
                .build();
    }
}
