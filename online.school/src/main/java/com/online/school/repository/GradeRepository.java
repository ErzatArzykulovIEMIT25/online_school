package com.online.school.repository;

import com.online.school.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<Grade> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);

    @Query("SELECT g FROM Grade g WHERE g.student.classEntity.id = :classId AND g.subject.id = :subjectId")
    List<Grade> findByClassIdAndSubjectId(Long classId, Long subjectId);

    List<Grade> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT g FROM Grade g WHERE g.teacher.id = :teacherId AND g.subject.id = :subjectId AND g.student.classEntity.id = :classId")
    List<Grade> findByTeacherIdAndSubjectIdAndClassId(Long teacherId, Long subjectId, Long classId);
}