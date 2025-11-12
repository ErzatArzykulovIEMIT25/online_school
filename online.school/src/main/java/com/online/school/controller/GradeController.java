package com.online.school.controller;

import com.online.school.model.Grade;
import com.online.school.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> createGrade(@RequestBody CreateGradeRequest request, Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            Grade grade = gradeService.createGrade(
                    request.getValue(), request.getStudentId(), request.getSubjectId(),
                    teacherId, request.getScheduleId(), request.getDate(), request.getComment()
            );
            return ResponseEntity.ok(grade);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateGrade(@PathVariable Long id, @RequestBody UpdateGradeRequest request) {
        Grade updated = gradeService.updateGrade(id, request.getValue(), request.getComment());
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentGrades(@PathVariable Long studentId, Authentication authentication) {
        try {
            Long currentUserId = getCurrentUserId(authentication);
            List<Grade> grades = gradeService.getStudentGrades(studentId, currentUserId);
            return ResponseEntity.ok(grades);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}/period")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> getStudentGradesByPeriod(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        try {
            Long currentUserId = getCurrentUserId(authentication);
            // Проверяем права доступа
            gradeService.getStudentGrades(studentId, currentUserId); // Это выбросит SecurityException если нет доступа
            List<Grade> grades = gradeService.getStudentGradesByPeriod(studentId, startDate, endDate);
            return ResponseEntity.ok(grades);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getGradesForTeacher(
            @RequestParam Long subjectId,
            @RequestParam Long classId,
            Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            List<Grade> grades = gradeService.getGradesForTeacher(teacherId, subjectId, classId);
            return ResponseEntity.ok(grades);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/class/{classId}/subject/{subjectId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<Grade>> getClassGrades(@PathVariable Long classId, @PathVariable Long subjectId) {
        return ResponseEntity.ok(gradeService.getClassGrades(classId, subjectId));
    }

    private Long getCurrentUserId(Authentication authentication) {
        // В реальном приложении здесь будет логика получения ID текущего пользователя
        // Пока возвращаем заглушку
        return 1L; // Заглушка - в реальном приложении получать из authentication
    }

    // DTOs
    public static class CreateGradeRequest {
        private Integer value;
        private Long studentId;
        private Long subjectId;
        private Long scheduleId;
        private LocalDate date;
        private String comment;

        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public Long getScheduleId() { return scheduleId; }
        public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class UpdateGradeRequest {
        private Integer value;
        private String comment;

        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }
}