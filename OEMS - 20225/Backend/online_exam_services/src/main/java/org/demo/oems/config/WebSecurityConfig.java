package org.demo.oems.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.demo.oems.utils.CommonConstantUtils.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http// 1. Enable CORS with your config
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:8080"));  // exact origin
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);  // cache preflight for 1 hour
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/v1/demo/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole(VALUE_ADMIN)
                        .requestMatchers("/api/v1/teacher/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER)
                        .requestMatchers("/api/v1/student/**").hasAnyRole(VALUE_STUDENT)
                        .requestMatchers("/api/v1/ai/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER)
                        .requestMatchers("/api/v1/user/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/class/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/exam/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/question/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/result/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/subject/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole(VALUE_ADMIN, VALUE_TEACHER, VALUE_STUDENT)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }




}
