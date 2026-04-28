package com.talha.academix.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.talha.academix.dto.LoginDTO;
import com.talha.academix.util.JwtService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final AuthenticationManager authMgr;
    private final JwtService jwtService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        // Authenticate the user
        Authentication authentication = authMgr.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

        // If successful, extract the user details
        var userDetails = (UserDetails) authentication.getPrincipal();

        // Extract the role. Spring Security adds "ROLE_" prefix by default.
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("STUDENT");

        // Generate the JWT token
        String token = jwtService.generateToken(userDetails.getUsername(), role);

        // Return token to the client
        return ResponseEntity.ok(Map.of(
                "token", token,
                "username", userDetails.getUsername(),
                "role", role));
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/enroll")
    public String enrollPage() {
        return "enroll";
    }

}