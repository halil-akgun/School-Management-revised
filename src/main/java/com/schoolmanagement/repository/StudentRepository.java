package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Student findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    @Query("select (count(s)>0) from Student s")
    boolean findStudent();

    @Query("select max(s.studentNumber) from Student s")
    int getMaxStudentNumber();

    List<Student> getStudentByNameContaining(String studentName);

    Optional<Student> findByUsername(String username);

    @Query("select s from Student s where s.advisorTeacher.teacher.username = :username")
// @Query(value= "SELECT s FROM Student s JOIN s.advisorTeacher at JOIN at.teacher t WHERE t.username=:username")
    List<Student> getAllStudentByAdvisorTeacher_Username(String username);

    @Query("SELECT s FROM Student s WHERE s.id IN :id")
    List<Student> findByIdsEquals(Long[] id);

    @Query("SELECT s FROM Student s WHERE s.username = :username")
    Optional<Student> findByUsernameEqualsForOptional(String username);

    @Modifying // Query DB'de degisiklik yapacaksa kullanilir
    @Query("DELETE FROM Student s WHERE s.id = :id")
    void deleteById(@Param("id") Long id); // deleteById methodunu override etmis olduk

    @Query("SELECT s FROM Student s WHERE s.id IN :id")
    Set<Student> findByIdsEquals(List<Long> id);
}
