package com.schoolmanagement.entity.abstracts;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schoolmanagement.entity.concretes.UserRole;
import com.schoolmanagement.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@MappedSuperclass // db'de olusacak child'larin tablolarinda buradaki fieldlar olussun
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder // child class'larda Builder ozelligi kullanilabilsin
public abstract class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String ssn;

    private String name;

    private String surname;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private String birthPlace;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // sadece db'ye giderken (yani write) esnasinda kullanilir
    private String password;

    @Column(unique = true)
    private String phoneNumber;

    @OneToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // sadece db'ye giderken (yani write) esnasinda kullanilir
    private UserRole userRole;

    private Gender gender;

}
