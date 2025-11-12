package com.online.school.controller;

import com.online.school.model.ClassEntity;
import com.online.school.model.Student;
import com.online.school.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<ClassEntity>> getAllClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        ClassEntity classEntity = classService.getClassById(id);
        return classEntity != null ? ResponseEntity.ok(classEntity) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClass(@RequestBody CreateClassRequest request) {
        try {
            ClassEntity classEntity = classService.createClass(
                    request.getGrade(), request.getLetter(), request.getAcademicYear());
            return ResponseEntity.ok(classEntity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody UpdateClassRequest request) {
        try {
            ClassEntity updated = classService.updateClass(
                    id, request.getGrade(), request.getLetter(), request.getAcademicYear());
            return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/rename")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> renameClass(@PathVariable Long id, @RequestParam String newLetter) {
        try {
            ClassEntity updated = classService.renameClass(id, newLetter);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            classService.deleteClass(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteStudents(@RequestParam String currentYear, @RequestParam String nextYear) {
        try {
            classService.promoteStudentsToNextYear(currentYear, nextYear);
            return ResponseEntity.ok("Students promoted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> transferStudent(@RequestParam Long studentId, @RequestParam Long targetClassId) {
        try {
            classService.transferStudent(studentId, targetClassId);
            return ResponseEntity.ok("Student transferred successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<List<Student>> getClassStudents(@PathVariable Long id) {
        return ResponseEntity.ok(classService.getClassStudents(id));
    }

    // DTOs
    public static class CreateClassRequest {
        private Integer grade;
        private String letter;
        private String academicYear;

        public Integer getGrade() { return grade; }
        public void setGrade(Integer grade) { this.grade = grade; }
        public String getLetter() { return letter; }
        public void setLetter(String letter) { this.letter = letter; }
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    }

    public static class UpdateClassRequest {
        private Integer grade;
        private String letter;
        private String academicYear;

        public Integer getGrade() { return grade; }
        public void setGrade(Integer grade) { this.grade = grade; }
        public String getLetter() { return letter; }
        public void setLetter(String letter) { this.letter = letter; }
        public String getAcademicYear() { return academicYear; }
        public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    }
}