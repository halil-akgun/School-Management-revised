package com.schoolmanagement.payload.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MeetRequestWithoutId {

    @NotNull(message = "Please enter description.")
    @Size(min = 2, max = 250, message = "Description should be at least 2 chars.")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Description must consist of the characters.")
    private String description;

    @NotNull(message = "Please enter date.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Future // tarihin gecmis tarih olarak girilmesini engeller
    private LocalDate date;

    @NotNull(message = "Please enter startTime.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime startTime;

    @NotNull(message = "Please enter stopTime.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "US")
    private LocalTime stopTime;

    @NotNull(message = "Please select students.")
    private Long[] studentIds;
}
