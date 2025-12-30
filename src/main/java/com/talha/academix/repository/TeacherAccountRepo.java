package com.talha.academix.repository;

import com.talha.academix.model.TeacherAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherAccountRepo extends JpaRepository<TeacherAccount, Long> {
    List<TeacherAccount> findAll();
    Optional<TeacherAccount> findByTeacher_Userid(Long teacherId);
    Optional<TeacherAccount> findByStripeAccountId(String stripeAccountId);
    boolean existsByTeacher_Userid(Long teacherId);
}