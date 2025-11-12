package com.online.school.controller;

import com.online.school.model.Schedule;
import com.online.school.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSchedule(@RequestBody CreateScheduleRequest request) {
        try {
            Schedule schedule = scheduleService.createSchedule(
                    request.getDayOfWeek(), request.getLessonNumber(), request.getClassId(),
                    request.getSubjectId(), request.getTeacherId(), request.getStartTime(), request.getEndTime()
            );
            return ResponseEntity.ok(schedule);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody UpdateScheduleRequest request) {
        Schedule updated = scheduleService.updateSchedule(
                id, request.getDayOfWeek(), request.getLessonNumber(),
                request.getStartTime(), request.getEndTime(), request.getIsActive()
        );
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Schedule>> getClassSchedule(@PathVariable Long classId) {
        return ResponseEntity.ok(scheduleService.getClassSchedule(classId));
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<List<Schedule>> getTeacherSchedule(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduleService.getTeacherSchedule(teacherId));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Schedule>> getSubjectSchedule(@PathVariable Long subjectId) {
        return ResponseEntity.ok(scheduleService.getSubjectSchedule(subjectId));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deactivateSchedule(@PathVariable Long id) {
        scheduleService.deactivateSchedule(id);
        return ResponseEntity.ok("Schedule deactivated");
    }

    // DTOs
    public static class CreateScheduleRequest {
        private DayOfWeek dayOfWeek;
        private Integer lessonNumber;
        private Long classId;
        private Long subjectId;
        private Long teacherId;
        private String startTime;
        private String endTime;

        public DayOfWeek getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public Integer getLessonNumber() { return lessonNumber; }
        public void setLessonNumber(Integer lessonNumber) { this.lessonNumber = lessonNumber; }
        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
    }

    public static class UpdateScheduleRequest {
        private DayOfWeek dayOfWeek;
        private Integer lessonNumber;
        private String startTime;
        private String endTime;
        private Boolean isActive;

        public DayOfWeek getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public Integer getLessonNumber() { return lessonNumber; }
        public void setLessonNumber(Integer lessonNumber) { this.lessonNumber = lessonNumber; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }
}