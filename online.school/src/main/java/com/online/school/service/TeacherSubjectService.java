package com.online.school.service;

import com.online.school.model.TeacherSubject;

import java.util.List;

public interface TeacherSubjectService {
    TeacherSubject assignSubjectToTeacher(Long teacherId, Long subjectId);
    void removeSubjectFromTeacher(Long teacherId, Long subjectId);
    List<TeacherSubject> getTeacherSubjects(Long teacherId);
    List<TeacherSubject> getSubjectTeachers(Long subjectId);
    boolean isTeacherAssignedToSubject(Long teacherId, Long subjectId);
}