package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.response.AdvisorTeacherResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.AdvisorTeacherRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorTeacherService {

    private final AdvisorTeacherRepository advisorTeacherRepository;
    private final UserRoleService userRoleService;

    public ResponseMessage<?> delete(Long id) {
        if (!advisorTeacherRepository.existsById(id))
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);

        advisorTeacherRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("AdvisorTeacher deleted.")
                .httpStatus(HttpStatus.OK).build();
    }

    public List<AdvisorTeacherResponse> getAll() {
        return advisorTeacherRepository.findAll().stream()
                .map(this::createAdvisorTeacherResponse).collect(Collectors.toList());
    }

    private AdvisorTeacherResponse createAdvisorTeacherResponse(AdvisorTeacher teacher) {
        return AdvisorTeacherResponse.builder()
                .teacherName(teacher.getTeacher().getName())
                .teacherSurName(teacher.getTeacher().getSurname())
                .teacherSsn(teacher.getTeacher().getSsn())
                .advisorTeacherId(teacher.getId())
                .build();
    }

    public Page<AdvisorTeacherResponse> search(int page, int size, String sort, String type) {
        Pageable pageable;
        if (Objects.equals(type, "desc"))
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return advisorTeacherRepository.findAll(pageable).map(this::createAdvisorTeacherResponse);
    }

    //    teacherService 'te kullaniliyor
    public void save(Teacher teacher) {
        AdvisorTeacher advisorTeacher = AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER))
                .build();

        advisorTeacherRepository.save(advisorTeacher);
    }

    public void update(boolean status, Teacher teacher) {
        Optional<AdvisorTeacher> advisorTeacher = advisorTeacherRepository.getAdvisorTeacherByTeacher_Id(teacher.getId());

        AdvisorTeacher.AdvisorTeacherBuilder advisorTeacherBuilder = AdvisorTeacher.builder()
                .teacher(teacher)
                .userRole(userRoleService.getUserRole(RoleType.ADVISORTEACHER));

        if (advisorTeacher.isPresent()) {
            if (status) {
                advisorTeacherBuilder.id(advisorTeacher.get().getId());
                advisorTeacherRepository.save(advisorTeacherBuilder.build());
            } else {
                advisorTeacherRepository.deleteById(advisorTeacher.get().getId());
            }
        } else {
            advisorTeacherRepository.save(advisorTeacherBuilder.build());
        }
    }

    //    StudentService icin method
    public Optional<AdvisorTeacher> getAdvisorTeacherById(Long id) {
        return advisorTeacherRepository.findById(id);
    }

    public Optional<AdvisorTeacher> getAdvisorTeacherByUsername(String username) {
        return advisorTeacherRepository.findByTeacher_UsernameEquals(username);
    }
}