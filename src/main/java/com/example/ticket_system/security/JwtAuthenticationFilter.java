package com.example.ticket_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Custom filter to intercept requests and validate JWT tokens
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        // Check if header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Extract JWT token

            try {
                // Validate and parse JWT token
                Claims claims = jwtTokenProvider.validateTokenAndGetClaims(jwtToken); // Changed method name

                // Extract user ID and roles from claims
                String userId = claims.getSubject();
                // Roles are usually stored as a comma-separated string or a list in JWT claims
                String role = claims.get("role", String.class); // Get custom 'role' claim

                // Create GrantedAuthority from role
                List<GrantedAuthority> authorities = Arrays.stream(role.split(","))
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                // Create authentication object
                // The principal here is the userId, which is used for ownership checks
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException e) {
                logger.error("JWT validation error in filter: " + e.getMessage());
                // Wrap JwtException in BadCredentialsException to ensure it's treated as an authentication failure
                // This exception will be caught by Spring Security's ExceptionTranslationFilter
                // and then handled by the authenticationEntryPoint configured in SecurityConfig.
                throw new BadCredentialsException("Invalid or expired token.", e);
            } catch (Exception e) {
                logger.error("An unexpected error occurred during JWT processing in filter: " + e.getMessage(), e);
                // For other unexpected errors, throw a generic AuthenticationException.
                // This will also be caught by the authenticationEntryPoint.
                throw new org.springframework.security.core.AuthenticationException("Unexpected authentication error.", e) {};
            }
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
