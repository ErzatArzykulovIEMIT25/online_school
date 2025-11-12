package com.online.school.service.impl;

import com.online.school.model.*;
import com.online.school.repository.*;
import com.online.school.service.AttendanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;

    @Override
    public Attendance markAttendance(Long studentId, Long subjectId, Long teacherId,
                                     LocalDate date, AttendanceStatus status, Long scheduleId) {
        log.info("Marking attendance for student: {}, subject: {}, date: {}", studentId, subjectId, date);

        // Проверяем существование сущностей
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        // Проверяем права учителя
        if (!canTeacherMarkAttendance(teacherId, subjectId)) {
            throw new SecurityException("Teacher is not authorized to mark attendance for this subject");
        }

        Schedule schedule = null;
        if (scheduleId != null) {
            schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        }

        // Проверяем, не отмечена ли уже посещаемость
        attendanceRepository.findByStudentIdAndSubjectIdAndDate(studentId, subjectId, date)
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Attendance already marked for this student and date");
                });

        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .markedBy(teacher)
                .date(date != null ? date : LocalDate.now())
                .status(status)
                .schedule(schedule)
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);
        log.info("Successfully marked attendance with id: {}", savedAttendance.getId());
        return savedAttendance;
    }

    @Override
    public Attendance updateAttendance(Long attendanceId, AttendanceStatus status) {
        log.info("Updating attendance with id: {}", attendanceId);

        return attendanceRepository.findById(attendanceId).map(attendance -> {
            attendance.setStatus(status);
            return attendanceRepository.save(attendance);
        }).orElse(null);
    }

    @Override
    public List<Attendance> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching attendance for student: {} from {} to {}", studentId, startDate, endDate);
        return attendanceRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate);
    }

    @Override
    public List<Attendance> getClassAttendance(Long classId, Long subjectId, LocalDate date) {
        log.info("Fetching attendance for class: {}, subject: {}, date: {}", classId, subjectId, date);
        return attendanceRepository.findByClassIdAndSubjectIdAndDate(classId, subjectId, date);
    }

    @Override
    public boolean canTeacherMarkAttendance(Long teacherId, Long subjectId) {
        return teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}