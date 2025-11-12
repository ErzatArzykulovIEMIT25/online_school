package com.online.school.controller;

import com.online.school.model.User;
import com.online.school.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> payload) {
        try {
            String firstName = (String) payload.get("firstName");
            String lastName = (String) payload.get("lastName");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");

            @SuppressWarnings("unchecked")
            Set<String> roles = ((List<String>) payload.getOrDefault("roles", List.of("STUDENT")))
                    .stream().collect(java.util.stream.Collectors.toSet());

            Integer grade = (payload.get("grade") != null) ? (Integer) payload.get("grade") : null;
            String letter = (String) payload.get("letter");
            String academicYear = (String) payload.get("academicYear");

            User newUser = userService.createUser(
                    firstName, lastName, email, password, roles, grade, letter, academicYear
            );

            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        User updated = userService.updateUser(id, req.getFirstName(), req.getLastName(), req.getEmail());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeRoles(@PathVariable Long id, @RequestBody RoleChangeRequest req) {
        if (req.getRoles() == null || req.getRoles().isEmpty()) {
            return ResponseEntity.badRequest().body("Roles list cannot be empty");
        }

        User updated = userService.changeUserRoles(id, req.getRoles());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // DTOs
    public static class UpdateUserRequest {
        private String firstName;
        private String lastName;
        private String email;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class RoleChangeRequest {
        private Set<String> roles;
        public Set<String> getRoles() { return roles; }
        public void setRoles(Set<String> roles) { this.roles = roles; }
    }
}