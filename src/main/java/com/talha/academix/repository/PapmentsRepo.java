package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Payments;

public interface PapmentsRepo extends JpaRepository<Payments, Long> {
    
}
