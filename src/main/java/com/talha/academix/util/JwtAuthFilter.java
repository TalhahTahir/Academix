package com.talha.academix.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip filter for public paths
        return path.equals("/") ||
                path.equals("/login") ||
                path.equals("/register") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.equals("/api/users/register") ||
                path.equals("/api/users/login") ||
                path.equals("/api/users/auth") ||
                path.equals("/api/users/welcome");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String jwt = null;
        String userName = null;

        // Extract JWT from cookies instead of the Authorization header
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null) {
            try {
                userName = jwtService.extractUsername(jwt);
            } catch (Exception ex) {
                // Invalid or malformed token
                userName = null;
            }
        }

        // If username is extracted and user is not already authenticated
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validate token
            if (!jwtService.isTokenExpired(jwt)) {
                // Prefer role from token if present
                String tokenRole = null;
                try {
                    tokenRole = jwtService.extractRole(jwt);
                } catch (Exception e) {
                    tokenRole = null;
                }
                
                Long userId = jwtService.extractUserId(jwt);

                List<GrantedAuthority> authorities;
                if (tokenRole != null && !tokenRole.isBlank()) {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tokenRole));
                } else {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT")); // Fallback
                }
                
                com.talha.academix.security.CustomUserDetails userDetails = 
                    new com.talha.academix.security.CustomUserDetails(userId, userName, authorities);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
