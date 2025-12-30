package com.talha.academix.repository;

import com.talha.academix.model.TeacherAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherAccountRepo extends JpaRepository<TeacherAccount, Long> {
    Optional<TeacherAccount> findByTeacher_Id(Long teacherId);
    Optional<TeacherAccount> findByStripeAccountId(String stripeAccountId);
    boolean existsByTeacher_Id(Long teacherId);
}