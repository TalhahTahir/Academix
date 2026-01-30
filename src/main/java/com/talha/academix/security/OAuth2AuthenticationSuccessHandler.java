package com.talha.academix.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.talha.academix.enums.Role;
import com.talha.academix.model.User;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.util.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public OAuth2AuthenticationSuccessHandler(JwtService jwtService, UserRepo userRepo, @Lazy PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Debug: print all attributes returned by GitHub
        System.out.println("OAuth2 attributes: " + oAuth2User.getAttributes());

        // GitHub provides "login" as the username, but check for numeric value
        String username = oAuth2User.getAttribute("login");
        String email = oAuth2User.getAttribute("email");

        System.out.println("DEBUG: OAuth2 username (login): " + username);
        // If login is numeric, try to use 'name' or 'email' instead
        if (username != null && username.matches("\\d+")) {
            String realName = oAuth2User.getAttribute("name");
            if (realName != null && !realName.isBlank() && !realName.matches("\\d+")) {
                username = realName;
            } else if (email != null && !email.isBlank()) {
                String emailName = email.split("@")[0];
                if (!emailName.matches("\\d+")) {
                    username = emailName;
                } else {
                    username = null;
                }
            } else {
                username = null;
            }
            System.out.println("DEBUG: Adjusted OAuth2 username: " + username);
        }

        // Fallbacks if username is still null or blank or numeric
        if (username == null || username.isBlank() || username.matches("\\d+")) {
            throw new RuntimeException("Could not determine a valid username from OAuth2 provider. Please ensure your GitHub account has a username or name set.");
        }

        // Create or find user in database
        User existingUser = userRepo.findByUsername(username);
        if (existingUser == null) {
            // Create new user from OAuth2
            User newUser = new User();
            newUser.setUsername(username);
            System.out.println("DEBUG: name for new user: " + newUser.getUsername());
            newUser.setEmail(email != null ? email : username + "@github.com");
            newUser.setPassword(passwordEncoder.encode("oauth2_" + System.currentTimeMillis()));
            newUser.setRole(Role.STUDENT); // Default role for OAuth2 users
            userRepo.save(newUser);
        }

        // Determine role to include in token
        String roleStr;
        if (existingUser != null) {
            roleStr = existingUser.getRole().name();
        } else {
            roleStr = Role.STUDENT.name();
        }

        // Generate JWT token
        System.out.println("DEBUG: Generating token for user: " + username + " with role: " + roleStr);
        String token = jwtService.generateToken(username, roleStr);

        // Redirect to frontend with token in URL (frontend will store it)
        String redirectUrl = "/oauth2/callback?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8) 
                            + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8);
        System.out.println("DEBUG: Redirect URL: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}
