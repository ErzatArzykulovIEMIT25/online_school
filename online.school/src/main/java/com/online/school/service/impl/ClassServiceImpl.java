package com.online.school.service.impl;

import com.online.school.model.ClassEntity;
import com.online.school.model.Student;
import com.online.school.repository.ClassRepository;
import com.online.school.repository.StudentRepository;
import com.online.school.service.ClassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;

    @Override
    public List<ClassEntity> getAllClasses() {
        log.info("Fetching all classes");
        return classRepository.findAll();
    }

    @Override
    public ClassEntity getClassById(Long id) {
        log.info("Fetching class by id: {}", id);
        return classRepository.findById(id).orElse(null);
    }

    @Override
    public ClassEntity createClass(Integer grade, String letter, String academicYear) {
        log.info("Creating class: {}{} for academic year {}", grade, letter, academicYear);

        validateClassData(grade, letter, academicYear);

        // Проверка уникальности
        if (classRepository.existsByGradeAndLetterAndAcademicYear(grade, letter, academicYear)) {
            throw new IllegalArgumentException("Class already exists: " + grade + letter + " for year " + academicYear);
        }

        ClassEntity classEntity = ClassEntity.builder()
                .grade(grade)
                .letter(letter)
                .academicYear(academicYear)
                .build();

        ClassEntity savedClass = classRepository.save(classEntity);
        log.info("Successfully created class with id: {}", savedClass.getId());
        return savedClass;
    }

    @Override
    public ClassEntity updateClass(Long id, Integer grade, String letter, String academicYear) {
        log.info("Updating class with id: {}", id);

        return classRepository.findById(id).map(classEntity -> {
            validateClassData(grade, letter, academicYear);

            // Проверка уникальности (кроме текущего класса)
            if (!classEntity.getGrade().equals(grade) || !classEntity.getLetter().equals(letter) ||
                    !classEntity.getAcademicYear().equals(academicYear)) {

                if (classRepository.existsByGradeAndLetterAndAcademicYear(grade, letter, academicYear)) {
                    throw new IllegalArgumentException("Class already exists: " + grade + letter + " for year " + academicYear);
                }
            }

            classEntity.setGrade(grade);
            classEntity.setLetter(letter);
            classEntity.setAcademicYear(academicYear);

            return classRepository.save(classEntity);
        }).orElse(null);
    }

    @Override
    public ClassEntity renameClass(Long classId, String newLetter) {
        log.info("Renaming class {} to {}", classId, newLetter);

        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        if (!newLetter.matches("[А-Я]")) {
            throw new IllegalArgumentException("Letter must be a single Cyrillic character (А-Я)");
        }

        // Проверяем, существует ли уже класс с такой буквой в том же году и классе
        if (classRepository.existsByGradeAndLetterAndAcademicYear(
                classEntity.getGrade(), newLetter, classEntity.getAcademicYear())) {
            throw new IllegalArgumentException("Class " + classEntity.getGrade() + newLetter + " already exists");
        }

        classEntity.setLetter(newLetter);
        return classRepository.save(classEntity);
    }

    @Override
    public void deleteClass(Long classId) {
        log.info("Deleting class with id: {}", classId);

        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        // Проверяем, есть ли студенты в классе
        if (!classEntity.getStudents().isEmpty()) {
            throw new IllegalStateException("Cannot delete class with students. Transfer students first.");
        }

        classRepository.delete(classEntity);
        log.info("Successfully deleted class with id: {}", classId);
    }

    @Override
    public void promoteStudentsToNextYear(String currentAcademicYear, String nextAcademicYear) {
        log.info("Promoting students from {} to {}", currentAcademicYear, nextAcademicYear);

        List<ClassEntity> currentClasses = classRepository.findByAcademicYear(currentAcademicYear);

        for (ClassEntity currentClass : currentClasses) {
            if (currentClass.getGrade() < 12) { // Максимум 12 класс
                Integer nextGrade = currentClass.getGrade() + 1;

                // Создаем или получаем класс следующего года
                ClassEntity nextYearClass = classRepository
                        .findByGradeAndLetterAndAcademicYear(nextGrade, currentClass.getLetter(), nextAcademicYear)
                        .orElseGet(() -> createClass(nextGrade, currentClass.getLetter(), nextAcademicYear));

                // Переводим студентов
                for (Student student : currentClass.getStudents()) {
                    student.setClassEntity(nextYearClass);
                    studentRepository.save(student);
                    log.debug("Transferred student {} to class {}", student.getId(), nextYearClass.getClassName());
                }
            } else {
                log.info("Class {} reached maximum grade, students will graduate", currentClass.getClassName());
            }
        }

        log.info("Successfully promoted all students");
    }

    @Override
    public void transferStudent(Long studentId, Long targetClassId) {
        log.info("Transferring student {} to class {}", studentId, targetClassId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        ClassEntity targetClass = classRepository.findById(targetClassId)
                .orElseThrow(() -> new IllegalArgumentException("Target class not found"));

        student.setClassEntity(targetClass);
        studentRepository.save(student);
        log.info("Successfully transferred student {} to class {}", studentId, targetClass.getClassName());
    }

    @Override
    public List<Student> getClassStudents(Long classId) {
        log.info("Fetching students for class id: {}", classId);
        return studentRepository.findByClassEntityId(classId);
    }

    @Override
    public boolean classExists(Integer grade, String letter, String academicYear) {
        return classRepository.existsByGradeAndLetterAndAcademicYear(grade, letter, academicYear);
    }

    private void validateClassData(Integer grade, String letter, String academicYear) {
        if (grade == null || grade < 1 || grade > 12) {
            throw new IllegalArgumentException("Grade must be between 1 and 12");
        }
        if (letter == null || !letter.matches("[А-Я]")) {
            throw new IllegalArgumentException("Letter must be a single Cyrillic character (А-Я)");
        }
        if (academicYear == null || academicYear.isBlank()) {
            throw new IllegalArgumentException("Academic year is required");
        }
    }
}