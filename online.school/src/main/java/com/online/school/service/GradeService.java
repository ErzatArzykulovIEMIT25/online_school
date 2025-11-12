package com.online.school.service;

import com.online.school.model.Grade;

import java.time.LocalDate;
import java.util.List;

public interface GradeService {
    Grade createGrade(Integer value, Long studentId, Long subjectId, Long teacherId, Long scheduleId, LocalDate date, String comment);
    Grade updateGrade(Long gradeId, Integer value, String comment);
    void deleteGrade(Long gradeId);
    List<Grade> getStudentGrades(Long studentId, Long requestingUserId);
    List<Grade> getGradesForTeacher(Long teacherId, Long subjectId, Long classId);
    List<Grade> getClassGrades(Long classId, Long subjectId);
    List<Grade> getStudentGradesByPeriod(Long studentId, LocalDate startDate, LocalDate endDate);
    boolean canTeacherGradeSubject(Long teacherId, Long subjectId);
}