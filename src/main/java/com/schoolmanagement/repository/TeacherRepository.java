package com.schoolmanagement.repository;

import com.schoolmanagement.entity.concretes.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByUsername(String username);

    boolean existsBySsn(String ssn);

    boolean existsByPhoneNumber(String phone);

    Teacher findByUsernameEquals(String username);

    boolean existsByEmail(String email);

    List<Teacher> getTeacherByNameContaining(String teacherName);

    Optional<Teacher> findByUsername(String username);

    @Query("SELECT t FROM Teacher t WHERE t.id IN :teacherIds")
    Set<Teacher> findByIdsEquals(List<Long> teacherIds);
}
