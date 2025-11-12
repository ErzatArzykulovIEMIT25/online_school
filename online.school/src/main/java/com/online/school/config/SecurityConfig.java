package com.online.school.config;

import com.online.school.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF для API
                .csrf(csrf -> csrf.disable())

                // Настройка доступа к эндпоинтам
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/create").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/classes/**").authenticated()
                        .requestMatchers("/api/subjects/**").authenticated()
                        .requestMatchers("/api/grades/**").authenticated()
                        .requestMatchers("/api/schedules/**").authenticated()
                        .requestMatchers("/api/attendance/**").authenticated()
                        .anyRequest().authenticated()
                )

                // Используем HTTP Basic аутентификацию
                .httpBasic(basic -> {})

                // Настройка сессий - stateless для API
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Подключаем свой UserDetailsService
                .userDetailsService(userDetailsService)

                // Отключаем frameOptions для H2 Console
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}