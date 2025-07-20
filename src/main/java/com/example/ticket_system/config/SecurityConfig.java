package com.example.ticket_system.config;

import com.example.ticket_system.exception.ErrorResponse;
import com.example.ticket_system.security.JwtAuthenticationFilter;
import com.example.ticket_system.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // Helper method to create a configured ObjectMapper
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 for dates
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print JSON
        return mapper;
    }

    // Configures the security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        // AuthenticationEntryPoint handles 401 Unauthorized errors (unauthenticated access or invalid token)
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("AuthenticationEntryPoint triggered for: " + authException.getMessage()); // ADDED LOG
                            HttpStatus status = HttpStatus.UNAUTHORIZED;
                            String errorMessage = "Authentication required or token is invalid/expired.";

                            response.setStatus(status.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            // Use ObjectMapper to write the JSON response to the output stream
                            objectMapper().writeValue(response.getWriter(), new ErrorResponse(
                                    LocalDateTime.now(),
                                    status.value(),
                                    status.getReasonPhrase(),
                                    errorMessage,
                                    request.getRequestURI()
                            ));
                            response.getWriter().flush(); // Ensure the response is sent immediately
                        })
                        // AccessDeniedHandler handles 403 Forbidden errors (authenticated user but no permission - 403)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            System.out.println("AccessDeniedHandler triggered for: " + accessDeniedException.getMessage()); // ADDED LOG
                            HttpStatus status = HttpStatus.FORBIDDEN;
                            String errorMessage = "You do not have permission to access this resource.";

                            response.setStatus(status.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");

                            objectMapper().writeValue(response.getWriter(), new ErrorResponse(
                                    LocalDateTime.now(),
                                    status.value(),
                                    status.getReasonPhrase(),
                                    errorMessage,
                                    request.getRequestURI()
                            ));
                            response.getWriter().flush(); // Ensure the response is sent immediately
                        })
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configures the AuthenticationManager for user authentication
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }
}
