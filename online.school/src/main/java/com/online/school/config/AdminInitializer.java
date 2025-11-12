package com.online.school.config;

import com.online.school.model.Role;
import com.online.school.model.User;
import com.online.school.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@school.com").isEmpty()) {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@school.com")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of(Role.ADMIN))
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin@school.com / admin123");
        }

        if (userRepository.findByEmail("teacher@school.com").isEmpty()) {
            User teacher = User.builder()
                    .firstName("John")
                    .lastName("Teacher")
                    .email("teacher@school.com")
                    .password(passwordEncoder.encode("teacher123"))
                    .roles(Set.of(Role.TEACHER))
                    .build();
            userRepository.save(teacher);
            System.out.println("✅ Teacher user created: teacher@school.com / teacher123");
        }
    }
}