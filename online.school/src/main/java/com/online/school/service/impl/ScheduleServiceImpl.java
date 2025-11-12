package com.online.school.service.impl;

import com.online.school.model.*;
import com.online.school.repository.*;
import com.online.school.service.ScheduleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public Schedule createSchedule(DayOfWeek dayOfWeek, Integer lessonNumber, Long classId,
                                   Long subjectId, Long teacherId, String startTime, String endTime) {
        log.info("Creating schedule for class: {}, day: {}, lesson: {}", classId, dayOfWeek, lessonNumber);

        // Проверяем существование сущностей
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        // Проверяем, может ли учитель вести этот предмет
        if (!teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId)) {
            throw new IllegalArgumentException("Teacher cannot teach this subject");
        }

        // Парсим время
        LocalTime start = parseTime(startTime);
        LocalTime end = parseTime(endTime);

        // Проверяем корректность времени
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Проверяем конфликты в расписании
        checkForScheduleConflicts(classId, teacherId, dayOfWeek, lessonNumber);

        Schedule schedule = Schedule.builder()
                .dayOfWeek(dayOfWeek)
                .lessonNumber(lessonNumber)
                .startTime(start)
                .endTime(end)
                .classEntity(classEntity)
                .subject(subject)
                .teacher(teacher)
                .isActive(true)
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Successfully created schedule with id: {}", savedSchedule.getId());
        return savedSchedule;
    }

    @Override
    public Schedule updateSchedule(Long scheduleId, DayOfWeek dayOfWeek, Integer lessonNumber,
                                   String startTime, String endTime, Boolean isActive) {
        log.info("Updating schedule with id: {}", scheduleId);

        return scheduleRepository.findById(scheduleId).map(schedule -> {
            if (dayOfWeek != null) {
                schedule.setDayOfWeek(dayOfWeek);
            }
            if (lessonNumber != null) {
                schedule.setLessonNumber(lessonNumber);
            }
            if (startTime != null) {
                schedule.setStartTime(parseTime(startTime));
            }
            if (endTime != null) {
                schedule.setEndTime(parseTime(endTime));
            }
            if (isActive != null) {
                schedule.setIsActive(isActive);
            }

            // Проверяем конфликты при изменении
            if (dayOfWeek != null || lessonNumber != null) {
                checkForScheduleConflicts(
                        schedule.getClassEntity().getId(),
                        schedule.getTeacher().getId(),
                        schedule.getDayOfWeek(),
                        schedule.getLessonNumber(),
                        scheduleId
                );
            }

            return scheduleRepository.save(schedule);
        }).orElse(null);
    }

    @Override
    public void deleteSchedule(Long scheduleId) {
        log.info("Deleting schedule with id: {}", scheduleId);
        scheduleRepository.deleteById(scheduleId);
    }

    @Override
    public List<Schedule> getClassSchedule(Long classId) {
        log.info("Fetching schedule for class: {}", classId);
        return scheduleRepository.findByClassEntityIdAndIsActiveTrueOrderByDayOfWeekAscLessonNumberAsc(classId);
    }

    @Override
    public List<Schedule> getTeacherSchedule(Long teacherId) {
        log.info("Fetching schedule for teacher: {}", teacherId);
        return scheduleRepository.findByTeacherIdAndIsActiveTrue(teacherId);
    }

    @Override
    public List<Schedule> getSubjectSchedule(Long subjectId) {
        log.info("Fetching schedule for subject: {}", subjectId);
        return scheduleRepository.findBySubjectIdAndIsActiveTrue(subjectId);
    }

    @Override
    public void deactivateSchedule(Long scheduleId) {
        log.info("Deactivating schedule with id: {}", scheduleId);
        scheduleRepository.findById(scheduleId).ifPresent(schedule -> {
            schedule.setIsActive(false);
            scheduleRepository.save(schedule);
        });
    }

    private void checkForScheduleConflicts(Long classId, Long teacherId, DayOfWeek dayOfWeek, Integer lessonNumber) {
        checkForScheduleConflicts(classId, teacherId, dayOfWeek, lessonNumber, null);
    }

    private void checkForScheduleConflicts(Long classId, Long teacherId, DayOfWeek dayOfWeek,
                                           Integer lessonNumber, Long excludeScheduleId) {
        // Проверка конфликта для класса
        scheduleRepository.findByClassIdAndDayAndLesson(classId, dayOfWeek, lessonNumber)
                .ifPresent(conflict -> {
                    if (excludeScheduleId == null || !conflict.getId().equals(excludeScheduleId)) {
                        throw new IllegalArgumentException("Class already has a lesson at this time");
                    }
                });

        // Проверка конфликта для учителя
        scheduleRepository.findByTeacherIdAndDayAndLesson(teacherId, dayOfWeek, lessonNumber)
                .ifPresent(conflict -> {
                    if (excludeScheduleId == null || !conflict.getId().equals(excludeScheduleId)) {
                        throw new IllegalArgumentException("Teacher already has a lesson at this time");
                    }
                });
    }

    private LocalTime parseTime(String timeString) {
        try {
            return LocalTime.parse(timeString, timeFormatter);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm format");
        }
    }
}