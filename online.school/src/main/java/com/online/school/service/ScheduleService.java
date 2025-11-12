package com.online.school.service;

import com.online.school.model.Schedule;


import java.time.DayOfWeek;
import java.util.List;

public interface ScheduleService {
    Schedule createSchedule(DayOfWeek dayOfWeek, Integer lessonNumber, Long classId,
                            Long subjectId, Long teacherId, String startTime, String endTime);
    Schedule updateSchedule(Long scheduleId, DayOfWeek dayOfWeek, Integer lessonNumber,
                            String startTime, String endTime, Boolean isActive);
    void deleteSchedule(Long scheduleId);
    List<Schedule> getClassSchedule(Long classId);
    List<Schedule> getTeacherSchedule(Long teacherId);
    List<Schedule> getSubjectSchedule(Long subjectId);
    void deactivateSchedule(Long scheduleId);
}