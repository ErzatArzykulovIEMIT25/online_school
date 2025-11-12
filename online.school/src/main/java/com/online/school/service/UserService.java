package com.online.school.service;

import com.online.school.model.User;

import java.util.List;
import java.util.Set;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User createUser(String firstName, String lastName, String email, String password,
                    Set<String> roles, Integer grade, String letter, String academicYear);
    User updateUser(Long id, String firstName, String lastName, String email);
    User changeUserRoles(Long id, Set<String> roles);
    void deleteUser(Long id);
    List<User> getUsersByRole(String role);
    boolean isTeacherForSubject(Long teacherId, Long subjectId);
}