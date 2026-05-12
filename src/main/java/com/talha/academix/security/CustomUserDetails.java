package com.talha.academix.security;

import java.util.Collection;
import java.util.List;

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
        authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
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
