package com.online.school.service.impl;

import com.online.school.model.*;
import com.online.school.repository.*;
import com.online.school.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional
    public User createUser(String firstName, String lastName, String email, String password,
                           Set<String> roles, Integer grade, String letter, String academicYear) {

        log.info("Creating user with email: {}", email);

        // Валидация email
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        // Валидация пароля
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Конвертация ролей
        Set<Role> roleSet = validateAndConvertRoles(roles);

        // Создание пользователя
        User user = User.builder()
                .firstName(firstName.trim())
                .lastName(lastName.trim())
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(password))
                .roles(roleSet)
                .build();

        User savedUser = userRepository.save(user);

        // Создание студента если роль STUDENT
        if (roleSet.contains(Role.STUDENT)) {
            createStudentForUser(savedUser, grade, letter, academicYear);
        }

        log.info("Successfully created user with id: {}", savedUser.getId());
        return savedUser;
    }

    private void createStudentForUser(User user, Integer grade, String letter, String academicYear) {
        if (grade == null || letter == null || academicYear == null) {
            throw new IllegalArgumentException("For STUDENT you must specify grade, letter and academicYear");
        }

        // Валидация класса
        validateClassData(grade, letter);

        // Поиск или создание класса
        ClassEntity classEntity = classRepository
                .findByGradeAndLetterAndAcademicYear(grade, letter, academicYear)
                .orElseGet(() -> {
                    ClassEntity newClass = ClassEntity.builder()
                            .grade(grade)
                            .letter(letter)
                            .academicYear(academicYear)
                            .build();
                    return classRepository.save(newClass);
                });

        // Создание студента
        Student student = Student.builder()
                .user(user)
                .classEntity(classEntity)
                .build();

        studentRepository.save(student);
    }

    @Override
    public User updateUser(Long id, String firstName, String lastName, String email) {
        log.info("Updating user with id: {}", id);

        return userRepository.findById(id).map(user -> {
            if (firstName != null && !firstName.isBlank()) {
                user.setFirstName(firstName.trim());
            }
            if (lastName != null && !lastName.isBlank()) {
                user.setLastName(lastName.trim());
            }
            if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
                if (userRepository.findByEmail(email).isPresent()) {
                    throw new IllegalArgumentException("Email already in use: " + email);
                }
                user.setEmail(email.trim().toLowerCase());
            }
            return userRepository.save(user);
        }).orElse(null);
    }

    @Override
    public User changeUserRoles(Long id, Set<String> roles) {
        log.info("Changing roles for user id: {}", id);

        return userRepository.findById(id).map(user -> {
            Set<Role> newRoles = validateAndConvertRoles(roles);
            user.setRoles(newRoles);

            // Если убрали роль STUDENT, удаляем связанного студента
            if (!newRoles.contains(Role.STUDENT) && user.getStudent() != null) {
                studentRepository.delete(user.getStudent());
                user.setStudent(null);
            }

            return userRepository.save(user);
        }).orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getUsersByRole(String role) {
        log.info("Fetching users by role: {}", role);
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            switch (roleEnum) {
                case TEACHER: return userRepository.findAllTeachers();
                case STUDENT: return userRepository.findAllStudents();
                default: return Collections.emptyList();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Override
    public boolean isTeacherForSubject(Long teacherId, Long subjectId) {
        return teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId);
    }

    private Set<Role> validateAndConvertRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of(Role.STUDENT);
        }

        return roles.stream()
                .map(role -> {
                    try {
                        return Role.valueOf(role.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid role: " + role);
                    }
                })
                .collect(Collectors.toSet());
    }

    private void validateClassData(Integer grade, String letter) {
        if (grade < 1 || grade > 12) {
            throw new IllegalArgumentException("Grade must be between 1 and 12");
        }
        if (!letter.matches("[А-Я]")) {
            throw new IllegalArgumentException("Letter must be a single Cyrillic character (А-Я)");
        }
    }
}