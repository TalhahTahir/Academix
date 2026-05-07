package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.repository.AttemptRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("attemptSecurity")
public class AttemptSecurity {
    
    private final AttemptRepo attemptRepo;

    public boolean isAttemptOwner(CustomUserDetails principal, Long attemptId) {
        if (principal == null || attemptId == null) {
            return false;
        }

        return attemptRepo.existsByAttemptIdAndStudent_Userid(attemptId, principal.getId());
    }
}
