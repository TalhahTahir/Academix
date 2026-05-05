package com.talha.academix.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.talha.academix.model.User;

public class CustomUserDetails implements UserDetails {

    private Long id;
    private String name;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        id = user.getUserid();
        name = user.getEmail();
        password = user.getPassword();
        authorities = Arrays.stream(user.getRole().name().split(","))
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }

    public CustomUserDetails(Long id, String name, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    public Long getId() {
        return id;
    }
}
