package com.talha.academix.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.talha.academix.model.User;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
User user = userRepo.findByUsername(name);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with name: " + name);
        }

return new CustomUserDetails(user);
    }
}
    