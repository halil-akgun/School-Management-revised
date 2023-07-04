package com.schoolmanagement.payload.dto;

import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import lombok.Data;

import java.util.Set;

@Data
public class LessonProgramDto {

    // DTO --> POJO
    public LessonProgram createLessonProgramFromRequest(LessonProgramRequest request, Set<Lesson> lessons) {
        return LessonProgram.builder()
                .startTime(request.getStartTime())
                .stopTime(request.getStopTime())
                .day(request.getDay())
                .lesson(lessons)
                .build();
    }
}
