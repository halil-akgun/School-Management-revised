package com.schoolmanagement.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagement.entity.enums.Day;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LessonProgramRequestForUpdate {

    @NotNull(message = "please enter the day")
    private Day day;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    @NotNull(message = "please enter the startTime")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    @NotNull(message = "please enter the stopTime")
    private LocalTime stopTime;

    @NotNull(message = "please select lesson")
    @Size(min = 1, message = "Lesson must not be empty")
    private List<Long> lessonIdList;

    @NotNull(message = "please enter the educationTerm")
    private Long educationTermId;

    @NotNull(message = "please select student")
    @Size(min = 1, message = "studentIdList must not be empty")
    private List<Long> studentIdList;

    @NotNull(message = "please select teacher")
    @Size(min = 1, message = "teacherIdList must not be empty")
    private List<Long> teacherIdList;
}
