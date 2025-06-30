package com.talha.academix.controllers;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping
    public CertificateDTO addCertificate(@RequestBody CertificateDTO dto) {
        return certificateService.addCertificate(dto);
    }

    @PutMapping("/{certificateId}")
    public CertificateDTO updateCertificate(@PathVariable Long certificateId, @RequestBody CertificateDTO dto) {
        return certificateService.updateCertificate(certificateId, dto);
    }

    @GetMapping("/{certificateId}")
    public CertificateDTO getCertificateById(@PathVariable Long certificateId) {
        return certificateService.getCertificateById(certificateId);
    }

    @GetMapping("/student/{studentId}")
    public List<CertificateDTO> getCertificatesByStudent(@PathVariable Long studentId) {
        return certificateService.getCertificatesByStudent(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<CertificateDTO> getCertificatesByCourse(@PathVariable Long courseId) {
        return certificateService.getCertificatesByCourse(courseId);
    }

    @DeleteMapping("/{certificateId}")
    public void deleteCertificate(@PathVariable Long certificateId) {
        certificateService.deleteCertificate(certificateId);
    }
}
