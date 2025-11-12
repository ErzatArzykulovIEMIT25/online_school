package com.online.school.repository;

import com.online.school.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByClassEntityIdAndIsActiveTrueOrderByDayOfWeekAscLessonNumberAsc(Long classId);
    List<Schedule> findByTeacherIdAndIsActiveTrue(Long teacherId);

    @Query("SELECT s FROM Schedule s WHERE s.classEntity.id = :classId AND s.dayOfWeek = :dayOfWeek AND s.lessonNumber = :lessonNumber AND s.isActive = true")
    Optional<Schedule> findByClassIdAndDayAndLesson(Long classId, DayOfWeek dayOfWeek, Integer lessonNumber);

    @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId AND s.dayOfWeek = :dayOfWeek AND s.lessonNumber = :lessonNumber AND s.isActive = true")
    Optional<Schedule> findByTeacherIdAndDayAndLesson(Long teacherId, DayOfWeek dayOfWeek, Integer lessonNumber);

    List<Schedule> findBySubjectIdAndIsActiveTrue(Long subjectId);
}