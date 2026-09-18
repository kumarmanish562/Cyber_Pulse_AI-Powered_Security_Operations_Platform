package cyberpulse.config;

import cyberpulse.auth.security.JwtAuthenticationFilter;
import cyberpulse.common.exception.RestAccessDeniedHandler;
import cyberpulse.common.exception.RestAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private final RestAccessDeniedHandler accessDeniedHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =====================================================
                // CORS
                // =====================================================
                .cors(Customizer.withDefaults())


                // =====================================================
                // CSRF
                // JWT based stateless API
                // =====================================================
                .csrf(csrf ->
                        csrf.disable()
                )


                // =====================================================
                // SESSION MANAGEMENT
                // Stateless authentication
                // =====================================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =====================================================
                // SECURITY HEADERS
                // =====================================================
                .headers(headers ->
                        headers

                                // Prevent MIME type sniffing
                                .contentTypeOptions(
                                        Customizer.withDefaults()
                                )

                                // Prevent clickjacking
                                .frameOptions(frame ->
                                        frame.deny()
                                )

                                // Referrer policy
                                .referrerPolicy(referrer ->
                                        referrer.policy(
                                                org.springframework
                                                        .security
                                                        .web
                                                        .header
                                                        .writers
                                                        .ReferrerPolicyHeaderWriter
                                                        .ReferrerPolicy
                                                        .NO_REFERRER
                                        )
                                )
                )


                // =====================================================
                // EXCEPTION HANDLING
                // =====================================================
                .exceptionHandling(exception ->
                        exception

                                // 401 Unauthorized
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )

                                // 403 Forbidden
                                .accessDeniedHandler(
                                        accessDeniedHandler
                                )
                )


                // =====================================================
                // AUTHORIZATION
                // =====================================================
                .authorizeHttpRequests(auth ->
                        auth

                                // -------------------------------------------------
                                // PUBLIC AUTHENTICATION ENDPOINTS
                                // -------------------------------------------------
                                .requestMatchers(
                                        "/api/v1/auth/register",
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/refresh"
                                )
                                .permitAll()


                                // -------------------------------------------------
                                // ACTUATOR HEALTH
                                // -------------------------------------------------
                                .requestMatchers(
                                        "/actuator/health",
                                        "/actuator/health/**"
                                )
                                .permitAll()


                                // -------------------------------------------------
                                // SWAGGER / OPENAPI
                                // Development / API documentation
                                // -------------------------------------------------
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                )
                                .permitAll()


                                // -------------------------------------------------
                                // EVERYTHING ELSE REQUIRES AUTHENTICATION
                                // -------------------------------------------------
                                .anyRequest()
                                .authenticated()
                )


                // =====================================================
                // JWT FILTER
                // =====================================================
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}