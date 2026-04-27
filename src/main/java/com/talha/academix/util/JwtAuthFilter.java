package com.talha.academix.util;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.talha.academix.security.CustomUserDetailService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailService userDetailService;

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

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                userName = jwtService.extractUsername(jwt);
            } catch (Exception ex) {
                // Invalid or malformed token: do not set authentication, continue filter chain
                userName = null;
            }
        }

        // If username is extracted and user is not already authenticated
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(userName);

            // Validate token
            if (!jwtService.isTokenExpired(jwt)) {
                // Prefer role from token if present, otherwise fall back to DB authorities
                String tokenRole = null;
                try {
                    tokenRole = jwtService.extractRole(jwt);
                } catch (Exception e) {
                    tokenRole = null;
                }

                List<GrantedAuthority> authorities;
                if (tokenRole != null && !tokenRole.isBlank()) {
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + tokenRole));
                } else {
                    authorities = (List<GrantedAuthority>) userDetails.getAuthorities();
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
