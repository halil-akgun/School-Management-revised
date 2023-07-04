package com.schoolmanagement.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // null olmayan field'lar json icinde client'a gidecek (response)
//              mesela bazilarini setleriz bazilarini setlemeyiz(null birakiriz) ve sadece setlediklerimiz gider
public class AuthResponse {

    private String username;
    private String ssn;
    private String role;
    private String token;
    private String name;
    private String isAdvisor;
}
