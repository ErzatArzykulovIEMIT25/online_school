package com.online.school.service.impl;

import com.online.school.model.TeacherSubject;
import com.online.school.model.User;
import com.online.school.model.Subject;
import com.online.school.model.Role;
import com.online.school.repository.TeacherSubjectRepository;
import com.online.school.repository.UserRepository;
import com.online.school.repository.SubjectRepository;
import com.online.school.service.TeacherSubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TeacherSubjectServiceImpl implements TeacherSubjectService {

    private final TeacherSubjectRepository teacherSubjectRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public TeacherSubject assignSubjectToTeacher(Long teacherId, Long subjectId) {
        log.info("Assigning subject {} to teacher {}", subjectId, teacherId);

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        // Проверяем, что пользователь действительно учитель
        if (!teacher.getRoles().contains(Role.TEACHER)) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        // Проверяем, не назначен ли уже предмет
        if (teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId)) {
            throw new IllegalArgumentException("Teacher is already assigned to this subject");
        }

        TeacherSubject teacherSubject = TeacherSubject.builder()
                .teacher(teacher)
                .subject(subject)
                .isActive(true)
                .build();

        TeacherSubject savedAssignment = teacherSubjectRepository.save(teacherSubject);
        log.info("Successfully assigned subject to teacher");
        return savedAssignment;
    }

    @Override
    public void removeSubjectFromTeacher(Long teacherId, Long subjectId) {
        log.info("Removing subject {} from teacher {}", subjectId, teacherId);

        TeacherSubject teacherSubject = teacherSubjectRepository
                .findByTeacherIdAndSubjectId(teacherId, subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        teacherSubjectRepository.delete(teacherSubject);
        log.info("Successfully removed subject from teacher");
    }

    @Override
    public List<TeacherSubject> getTeacherSubjects(Long teacherId) {
        log.info("Fetching subjects for teacher: {}", teacherId);
        return teacherSubjectRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<TeacherSubject> getSubjectTeachers(Long subjectId) {
        log.info("Fetching teachers for subject: {}", subjectId);
        return teacherSubjectRepository.findBySubjectId(subjectId);
    }

    @Override
    public boolean isTeacherAssignedToSubject(Long teacherId, Long subjectId) {
        return teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId);
    }
}