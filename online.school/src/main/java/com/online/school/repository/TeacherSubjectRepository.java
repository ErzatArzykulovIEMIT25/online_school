package com.online.school.repository;

import com.online.school.model.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    Optional<TeacherSubject> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    boolean existsByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    List<TeacherSubject> findByTeacherId(Long teacherId);
    List<TeacherSubject> findBySubjectId(Long subjectId);

    @Query("SELECT ts FROM TeacherSubject ts WHERE ts.teacher.id = :teacherId AND ts.isActive = true")
    List<TeacherSubject> findActiveByTeacherId(Long teacherId);
}