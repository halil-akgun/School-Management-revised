package com.schoolmanagement.payload.request.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@SuperBuilder
//@MappedSuperclass // db ile bir ilgisi olmadigindan gereksiz
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseUserRequest implements Serializable {

    @NotNull(message = "enter a username")
    @Size(min = 4, max = 16, message = "your username should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your username must consist of the characters .")
//    @Pattern(regexp = "\\A(?!\\s*\\Z).+" : bosluk olmamali, en az 1 char icermeli
    private String username;

    @NotNull(message = "enter a name")
    @Size(min = 2, max = 16, message = "your name should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your name must consist of the characters .")
    private String name;

    @NotNull(message = "enter a surname")
    @Size(min = 2, max = 16, message = "your surname should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your surname must consist of the characters .")
    private String surname;

    @NotNull(message = "enter your birthday")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Past // tarih bugunden eski mi ?
    private LocalDate birthDay;

    @NotNull(message = "enter your ssn")
    @Pattern(regexp = "^(?!000|666)[0-8][0-9]{2}-(?!00)[0-9]{2}-(?!0000)[0-9]{4}$",
            message = "Please enter valid SSN number")
    private String ssn;

    @NotNull(message = "enter your birthPlace")
    @Size(min = 2, max = 16, message = "your birthPlace should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your birthPlace must consist of the characters .")
    private String birthPlace;

    @NotNull(message = "enter your password")
    @Size(min = 8, max = 60, message = "your password should be at least 8 chars")
    private String password;

    @NotNull(message = "enter your phoneNumber")
    @Size(min = 12, max = 12, message = "your password should be at exact 12 chars")
    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    private String phoneNumber;

    @NotNull(message = "enter your gender")
    private Gender gender;
}
