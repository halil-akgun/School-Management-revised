package com.schoolmanagement.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class StudentInfoRequestWithoutTeacherId {

    @NotNull(message = "Please select Education Term")
    private Long educationTermId;

    @NotNull(message = "Please enter midTermExam")
    @DecimalMax("100.0")
    @DecimalMin("0.0")
    private Double midTermExam;

    @NotNull(message = "Please enter finalExam")
    @DecimalMax("100.0")
    @DecimalMin("0.0")
    private Double finalExam;

    @NotNull(message = "Please enter absentee")
    private Integer absentee;

    @NotNull(message = "Please enter infoNote")
    @Size(min = 10, max = 200, message = "info should be at least 10 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Info must consist of the characters .")
    private String infoNote;

    @NotNull(message = "Please select lessonId")
    private Long lessonId;

    @NotNull(message = "Please select studentId")
    private Long studentId;
}
