package com.online.school.repository;

import com.online.school.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Attendance a WHERE a.student.classEntity.id = :classId AND a.subject.id = :subjectId AND a.date = :date")
    List<Attendance> findByClassIdAndSubjectIdAndDate(Long classId, Long subjectId, LocalDate date);

    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(Long studentId, Long subjectId, LocalDate date);

    List<Attendance> findByMarkedById(Long teacherId);
}