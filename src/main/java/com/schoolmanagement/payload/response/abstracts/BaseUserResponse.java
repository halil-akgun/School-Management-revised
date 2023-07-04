package com.schoolmanagement.payload.response.abstracts;

import com.schoolmanagement.entity.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@SuperBuilder
//@MappedSuperclass // db ile bir ilgisi olmadigindan gereksiz
public class BaseUserResponse {

    private Long userId;
    private String username;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String ssn;
    private String birthPlace;
    private String phoneNumber;
    private Gender gender;
}
