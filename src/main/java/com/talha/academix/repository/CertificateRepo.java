package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Certificate;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {
}
