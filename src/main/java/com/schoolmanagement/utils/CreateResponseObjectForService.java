package com.schoolmanagement.utils;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.response.StudentResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateResponseObjectForService {

    public StudentResponse createStudentResponse(Student student) {
        return StudentResponse.builder()
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .email(student.getEmail())
                .name(student.getName())
                .surname(student.getSurname())
                .username(student.getUsername())
                .birthPlace(student.getBirthPlace())
                .ssn(student.getSsn())
                .gender(student.getGender())
                .birthday(student.getBirthday())
                .phoneNumber(student.getPhoneNumber())
                .userId(student.getId())
                .isActive(student.isActive())
                .build();
    }
}
