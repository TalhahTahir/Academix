package com.talha.academix.repository;

import com.talha.academix.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
