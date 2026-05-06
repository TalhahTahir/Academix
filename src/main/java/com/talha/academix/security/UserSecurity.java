package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.User;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("userSecurity")
public class UserSecurity {
    
    private final UserRepo userRepo;

    public boolean isUserOwner(CustomUserDetails principal, Long userId) {
        if (principal == null || userId == null) {
            return false;
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return user.getUserid().equals(principal.getId());
    }
}
