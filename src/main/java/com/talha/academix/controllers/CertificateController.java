package com.talha.academix.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talha.academix.dto.CertificateDTO;
import com.talha.academix.services.CertificateService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certificates")
public class CertificateController {
    private final CertificateService certificateService;

    @PostMapping("/enrollment/{id}")
    public CertificateDTO awardCertificate(@PathVariable("id") Long enrollmentId) {
        return certificateService.awardCertificate(enrollmentId);
    }

    @GetMapping("/all/student/{id}")
    public List<CertificateDTO> getStudentCertificates(@PathVariable("id") Long studentId) {
        return certificateService.getAllByStudent(studentId);
    }

    @GetMapping("/{id}")
    public CertificateDTO getCertificate(@PathVariable("id") Long certificateId) {
        return certificateService.getById(certificateId);
    }

    @GetMapping("/count")
    public Long countAll() {
        return certificateService.countAll();
    }

    @GetMapping("/count/student/{id}")
    public Long countAllByStudent(@PathVariable("id") Long studentId) {
        return certificateService.countAllByStudent(studentId);
    }

    @GetMapping("/count/course/{id}")
    public Long countAllByCourse(@PathVariable("id") Long courseId) {
        return certificateService.countAllByCourse(courseId);
    }

}
