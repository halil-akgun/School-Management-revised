package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface LessonProgramRepository extends JpaRepository<LessonProgram, Long> {
    List<LessonProgram> findByTeachers_IdNull();
/*
    findByTeachers_IdNull() metodu, LessonProgram sınıfına ait kayıtları Teachers ilişkisine göre filtrelemek
    için kullanılır. Bu metodun döndürdüğü liste, Teachers ilişkisine sahip olmayan veya Teachers ilişkisine
    sahip olup id alanı null olan LessonProgram nesnelerini içerir.
*/

    List<LessonProgram> findByTeachers_IdNotNull();

    @Query("SELECT l FROM LessonProgram l INNER JOIN l.teachers teachers WHERE teachers.username = ?1")
    Set<LessonProgram> getLessonProgramByTeacherUsername(String username);

    @Query("SELECT l FROM LessonProgram l INNER JOIN l.students students WHERE students.username = ?1")
    Set<LessonProgram> getLessonProgramByStudentUsername(String username);

    @Query("SELECT l FROM LessonProgram l WHERE l.id IN :lessonIdList")
    Set<LessonProgram> getLessonProgramByLessonProgramIdList(Set<Long> lessonIdList);
}
