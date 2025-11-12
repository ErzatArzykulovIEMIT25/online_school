package com.online.school.repository;

import com.online.school.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long> {
    Optional<ClassEntity> findByGradeAndLetterAndAcademicYear(Integer grade, String letter, String academicYear);
    List<ClassEntity> findByAcademicYear(String academicYear);
    boolean existsByGradeAndLetterAndAcademicYear(Integer grade, String letter, String academicYear);

    @Query("SELECT c FROM ClassEntity c WHERE c.grade = :grade AND c.academicYear = :academicYear")
    List<ClassEntity> findByGradeAndAcademicYear(Integer grade, String academicYear);
}