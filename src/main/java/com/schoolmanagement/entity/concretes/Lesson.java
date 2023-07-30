package com.schoolmanagement.entity.concretes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Lesson implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId;

    private String lessonName;

    @PositiveOrZero(message = "Credit score must be a non-negative value")
    private Integer creditScore;

    private Boolean isCompulsory; // zorunlu mu

    @ManyToMany(mappedBy = "lesson")
    private Set<LessonProgram> lessonPrograms;
}
