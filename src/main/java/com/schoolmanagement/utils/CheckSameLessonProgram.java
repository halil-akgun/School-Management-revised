package com.schoolmanagement.utils;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.exception.BadRequestException;

import java.util.HashSet;
import java.util.Set;

public class CheckSameLessonProgram {

    public static void checkLessonProgram(Set<LessonProgram> existsLessonProgram, Set<LessonProgram> lessonProgramRequest) {

        if (existsLessonProgram.isEmpty() && lessonProgramRequest.size() > 1) {
            checkDuplicateLessonPrograms(lessonProgramRequest);
        } else {
            checkDuplicateLessonPrograms(lessonProgramRequest);
            checkDuplicateLessonPrograms(existsLessonProgram, lessonProgramRequest);
        }
    }

    private static void checkDuplicateLessonPrograms(Set<LessonProgram> lessonProgramRequest) {

        Set<String> uniqueLessonProgramKeys = new HashSet<>();
        for (LessonProgram w : lessonProgramRequest) {
            String lessonProgramKey = w.getDay().name() + w.getStartTime();
            if (uniqueLessonProgramKeys.contains(lessonProgramKey))
                throw new BadRequestException(Messages.LESSON_PROGRAM_EXIST_MESSAGE);
            uniqueLessonProgramKeys.add(lessonProgramKey);
        }
    }

    private static void checkDuplicateLessonPrograms(Set<LessonProgram> existsLessonProgram, Set<LessonProgram> lessonProgramRequest) {

        for (LessonProgram r : lessonProgramRequest) {
            if (existsLessonProgram.stream().anyMatch(lessonProgram -> // lessonProgram yerine 't' de yazabiliriz
                    lessonProgram.getStartTime().equals(r.getStartTime()) &&
                            lessonProgram.getDay().equals(r.getDay()))) {
                throw new BadRequestException(Messages.LESSON_PROGRAM_EXIST_MESSAGE);
            }
        }
    }
}
