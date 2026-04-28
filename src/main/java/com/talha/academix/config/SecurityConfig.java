package com.talha.academix.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.talha.academix.util.JwtAuthFilter;
import com.talha.academix.security.CustomUserDetailService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(r -> r
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // Public API endpoints
                .requestMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/auth",
                        "/api/users/welcome",
                        "/stripe/webhook",
                        "/enroll"
                ).permitAll()

                // Public frontend pages
                .requestMatchers("/", "/login", "/register").permitAll()

                .anyRequest().authenticated()
            )

            .userDetailsService(customUserDetailService)

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // PROPER LOGOUT
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // frontend must call this
                .addLogoutHandler((request, response, authentication) -> {

                    // 1. Invalidate server session
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.invalidate();
                    }

                    // 2. Clear SecurityContext
                    SecurityContextHolder.clearContext();

                    // 3. Delete JSESSIONID cookie
                    Cookie jsession = new Cookie("JSESSIONID", null);
                    jsession.setPath("/");
                    jsession.setHttpOnly(true);
                    jsession.setMaxAge(0);
                    response.addCookie(jsession);

                    // 4. (Optional) delete JWT cookie if you ever set one
                    Cookie jwt = new Cookie("token", null);
                    jwt.setPath("/");
                    jwt.setMaxAge(0);
                    response.addCookie(jwt);
                })
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"logout\": \"success\"}");
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configs) throws Exception {
        return configs.getAuthenticationManager();
    }
}
