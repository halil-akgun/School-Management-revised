package com.schoolmanagement.payload.request;

import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class StudentRequest extends BaseUserRequest {

    @NotNull(message = "enter mother name")
    @Size(min = 2, max = 16, message = "your mother name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your mother name must consist of the characters a-z and 0-9.")
    private String motherName;

    @NotNull(message = "enter father name")
    @Size(min = 2, max = 16, message = "your father name should be at least 2 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your father name must consist of the characters a-z and 0-9.")
    private String fatherName;

    @NotNull(message = "enter your email")
    @Size(min = 5, max = 50, message = "your email should be at least 5 chars")
    @Email(message = "enter valid email")
    private String email;

    @NotNull(message = "select advisorTeacher")
    private Long advisorTeacherId;
}
