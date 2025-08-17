package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.Role;
import com.talha.academix.model.User;

public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    long countByRole(Role role);
}
