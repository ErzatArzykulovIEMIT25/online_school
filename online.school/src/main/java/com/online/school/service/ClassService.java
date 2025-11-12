package com.online.school.service;

import com.online.school.model.ClassEntity;
import com.online.school.model.Student;

import java.util.List;

public interface ClassService {
    List<ClassEntity> getAllClasses();
    ClassEntity getClassById(Long id);
    ClassEntity createClass(Integer grade, String letter, String academicYear);
    ClassEntity updateClass(Long id, Integer grade, String letter, String academicYear);
    ClassEntity renameClass(Long classId, String newLetter);
    void deleteClass(Long classId);
    void promoteStudentsToNextYear(String currentAcademicYear, String nextAcademicYear);
    void transferStudent(Long studentId, Long targetClassId);
    List<Student> getClassStudents(Long classId);
    boolean classExists(Integer grade, String letter, String academicYear);
}