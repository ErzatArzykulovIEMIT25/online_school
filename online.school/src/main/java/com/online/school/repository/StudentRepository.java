package com.online.school.repository;

import com.online.school.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    List<Student> findByClassEntityId(Long classId);

    @Query("SELECT s FROM Student s WHERE s.classEntity.grade = :grade AND s.classEntity.letter = :letter AND s.classEntity.academicYear = :academicYear")
    List<Student> findByGradeAndLetterAndAcademicYear(Integer grade, String letter, String academicYear);
}