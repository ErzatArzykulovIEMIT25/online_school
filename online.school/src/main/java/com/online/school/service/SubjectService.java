package com.online.school.service;

import com.online.school.model.Subject;

import java.util.List;

public interface SubjectService {
    List<Subject> getAllSubjects();
    Subject getSubjectById(Long id);
    Subject createSubject(String name, String description);
    Subject updateSubject(Long id, String name, String description);
    void deleteSubject(Long id);
    boolean subjectExists(String name);
}