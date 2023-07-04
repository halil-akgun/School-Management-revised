package com.schoolmanagement.payload.dto;

import com.schoolmanagement.entity.concretes.ViceDean;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import lombok.Data;

@Data
public class ViceDeanDto {

    public ViceDean fromDtoToViceDean(ViceDeanRequest request) {
        return ViceDean.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .ssn(request.getSsn())
                .phoneNumber(request.getPhoneNumber())
                .username(request.getUsername())
                .birthday(request.getBirthDay())
                .birthPlace(request.getBirthPlace())
                .gender(request.getGender())
                .password(request.getPassword())
                .build();
    }
}
