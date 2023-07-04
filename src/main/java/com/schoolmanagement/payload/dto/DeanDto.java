package com.schoolmanagement.payload.dto;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.payload.request.DeanRequest;
import lombok.Data;

@Data
//@Component  // CreateObjectBean icinde bu classtan Bean olusturuldugu icin bu annotation iptal
// 2 sekilde bean olusturulur: 1- class uzerine Component veya @Configuration class'ta @Bean yardimi ile
public class DeanDto {

    //    DTO -> POJO
    public Dean dtoDean(DeanRequest deanRequest) {
        return Dean.builder()
                .username(deanRequest.getUsername())
                .name(deanRequest.getName())
                .surname(deanRequest.getSurname())
                .password(deanRequest.getPassword())
                .ssn(deanRequest.getSsn())
                .birthday(deanRequest.getBirthDay())
                .birthPlace(deanRequest.getBirthPlace())
                .phoneNumber(deanRequest.getPhoneNumber())
                .gender(deanRequest.getGender())
                .build();
    }
}
