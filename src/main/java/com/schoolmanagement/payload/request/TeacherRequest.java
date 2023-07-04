package com.schoolmanagement.payload.request;

import com.schoolmanagement.payload.request.abstracts.BaseUserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {

    @NotNull(message = "please select your email")
    @Email(message = "please enter a valid email")
    @Size(min = 5, max = 50, message = "your email should be between 5-50 chars")
    private String email;

    @NotNull(message = "please select isAdviser teacher")
    private boolean isAdviser = true;
    // 'is' ile basladigindan dolayi boolean olmali (Lombok(getter-setter) sorunlu)

    @NotNull(message = "please select lesson")
    private Set<Long> lessonsIdList;
}
