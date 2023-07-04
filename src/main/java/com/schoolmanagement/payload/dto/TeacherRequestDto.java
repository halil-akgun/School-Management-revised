package com.schoolmanagement.payload.dto;

import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.payload.request.TeacherRequest;
import lombok.Data;

@Data
public class TeacherRequestDto {

    public Teacher createTeacher(TeacherRequest request) {
        return Teacher.builder()
                .email(request.getEmail())
                .isAdvisor(request.isAdviser())
                .birthPlace(request.getBirthPlace())
                .phoneNumber(request.getPhoneNumber())
                .ssn(request.getSsn())
                .username(request.getUsername())
                .gender(request.getGender())
                .birthday(request.getBirthDay())
                .name(request.getName())
                .surname(request.getSurname())
                .build();
    }
}
