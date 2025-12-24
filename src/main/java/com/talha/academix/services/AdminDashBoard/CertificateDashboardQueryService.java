package com.talha.academix.services.AdminDashBoard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.repository.CertificateRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateDashboardQueryService {
    
    private final CertificateRepo certificateRepo;

    public AdminDashBoardDTO.Certificates certificatesSection(){
        return AdminDashBoardDTO.Certificates.builder()
                .totalCertificates(certificateRepo.count())
                .build();
    }
}
