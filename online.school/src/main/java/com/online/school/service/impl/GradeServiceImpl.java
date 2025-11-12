package com.online.school.service.impl;

import com.online.school.model.*;
import com.online.school.repository.*;
import com.online.school.service.GradeService;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;

    @Override
    public Grade createGrade(Integer value, Long studentId, Long subjectId, Long teacherId,
                             Long scheduleId, LocalDate date, String comment) {
        log.info("Creating grade for student: {}, subject: {}, teacher: {}", studentId, subjectId, teacherId);

        // Проверяем существование сущностей
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        // Проверяем права учителя
        if (!canTeacherGradeSubject(teacherId, subjectId)) {
            throw new SecurityException("Teacher is not authorized to grade this subject");
        }

        Schedule schedule = null;
        if (scheduleId != null) {
            schedule = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));
        }

        Grade grade = Grade.builder()
                .value(value)
                .student(student)
                .subject(subject)
                .teacher(teacher)
                .schedule(schedule)
                .date(date != null ? date : LocalDate.now())
                .comment(comment)
                .build();

        Grade savedGrade = gradeRepository.save(grade);
        log.info("Successfully created grade with id: {}", savedGrade.getId());
        return savedGrade;
    }

    @Override
    public Grade updateGrade(Long gradeId, Integer value, String comment) {
        log.info("Updating grade with id: {}", gradeId);

        return gradeRepository.findById(gradeId).map(grade -> {
            if (value != null) {
                grade.setValue(value);
            }
            if (comment != null) {
                grade.setComment(comment);
            }
            return gradeRepository.save(grade);
        }).orElse(null);
    }

    @Override
    public void deleteGrade(Long gradeId) {
        log.info("Deleting grade with id: {}", gradeId);
        gradeRepository.deleteById(gradeId);
    }

    @Override
    public List<Grade> getStudentGrades(Long studentId, Long requestingUserId) {
        log.info("Fetching grades for student: {} by user: {}", studentId, requestingUserId);

        // Проверяем права доступа
        if (!hasAccessToStudentGrades(studentId, requestingUserId)) {
            throw new SecurityException("Access denied to view these grades");
        }

        return gradeRepository.findByStudentId(studentId);
    }

    @Override
    public List<Grade> getGradesForTeacher(Long teacherId, Long subjectId, Long classId) {
        log.info("Fetching grades for teacher: {}, subject: {}, class: {}", teacherId, subjectId, classId);

        // Проверяем права учителя
        if (!canTeacherGradeSubject(teacherId, subjectId)) {
            throw new SecurityException("Teacher is not authorized for this subject");
        }

        return gradeRepository.findByTeacherIdAndSubjectIdAndClassId(teacherId, subjectId, classId);
    }

    @Override
    public List<Grade> getClassGrades(Long classId, Long subjectId) {
        log.info("Fetching grades for class: {}, subject: {}", classId, subjectId);
        return gradeRepository.findByClassIdAndSubjectId(classId, subjectId);
    }

    @Override
    public List<Grade> getStudentGradesByPeriod(Long studentId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching grades for student: {} from {} to {}", studentId, startDate, endDate);
        return gradeRepository.findByStudentIdAndDateBetween(studentId, startDate, endDate);
    }

    @Override
    public boolean canTeacherGradeSubject(Long teacherId, Long subjectId) {
        return teacherSubjectRepository.existsByTeacherIdAndSubjectId(teacherId, subjectId);
    }

    private boolean hasAccessToStudentGrades(Long studentId, Long requestingUserId) {
        // Студент может видеть только свои оценки
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return false;

        // Если запрашивающий - сам студент
        if (student.getUser().getId().equals(requestingUserId)) {
            return true;
        }

        // Если запрашивающий - учитель, проверяем может ли он ставить оценки этому студенту
        User requestingUser = userRepository.findById(requestingUserId).orElse(null);
        if (requestingUser != null && requestingUser.getRoles().contains(Role.TEACHER)) {
            // Учитель может видеть оценки студентов по своим предметам
            return true; // Более детальная проверка может быть добавлена
        }

        // Администраторы имеют полный доступ
        if (requestingUser != null && requestingUser.getRoles().contains(Role.ADMIN)) {
            return true;
        }

        return false;
    }
}