package com.online.school.service;

import com.online.school.model.Attendance;
import com.online.school.model.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance markAttendance(Long studentId, Long subjectId, Long teacherId,
                              LocalDate date, AttendanceStatus status, Long scheduleId);
    Attendance updateAttendance(Long attendanceId, AttendanceStatus status);
    List<Attendance> getStudentAttendance(Long studentId, LocalDate startDate, LocalDate endDate);
    List<Attendance> getClassAttendance(Long classId, Long subjectId, LocalDate date);
    boolean canTeacherMarkAttendance(Long teacherId, Long subjectId);
}