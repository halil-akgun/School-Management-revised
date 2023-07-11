package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.concretes.Teacher;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.TeacherRequestDto;
import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.CheckSameLessonProgram;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Mapper;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final LessonProgramService lessonProgramService;
    private final FieldControl fieldControl;
    private final PasswordEncoder passwordEncoder;
    private final TeacherRequestDto teacherRequestDto;
    private final UserRoleService userRoleService;
    private final AdvisorTeacherService advisorTeacherService;

    public ResponseMessage<TeacherResponse> save(TeacherRequest request) {

        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(request.getLessonsIdList());

        if (lessons.isEmpty())
            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);
        else {
            fieldControl.checkDuplicate(request.getUsername(), request.getSsn(),
                    request.getPhoneNumber(), request.getEmail());

            Teacher teacher = createTeacher(request);
            teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
            teacher.setLessonsProgramList(lessons);
            teacher.setPassword(passwordEncoder.encode(request.getPassword()));

            Teacher savedData = teacherRepository.save(teacher);

            if (savedData.isAdvisor())
                advisorTeacherService.save(savedData);

            return ResponseMessage.<TeacherResponse>builder()
                    .message("teacher created.")
                    .httpStatus(HttpStatus.CREATED)
                    .object(Mapper.teacherResponseFromTeacher(savedData)).build();
        }

    }

    private Teacher createTeacher(TeacherRequest request) {
        return teacherRequestDto.createTeacher(request);
    }

    public List<TeacherResponse> getAllTeacher() {
        return teacherRepository.findAll().stream().map(Mapper::teacherResponseFromTeacher).collect(Collectors.toList());
    }

    public ResponseMessage<TeacherResponse> update(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        Set<LessonProgram> lessons = lessonProgramService.getLessonProgramById(request.getLessonsIdList());

        if (lessons.size() == 0)
            throw new BadRequestException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);

        if (!checkParameterForUpdateMethod(teacher, request))
            fieldControl.checkDuplicate(request.getUsername(), request.getSsn(),
                    request.getPhoneNumber(), request.getEmail());

        Teacher updatedTeacher = Mapper.teacherFromTeacherRequest(request, id);
        updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
        updatedTeacher.setPassword(passwordEncoder.encode(request.getPassword()));
        updatedTeacher.setLessonsProgramList(lessons);

        Teacher savedData = teacherRepository.save(updatedTeacher);

        advisorTeacherService.update(request.isAdviser(), savedData);

        return ResponseMessage.<TeacherResponse>builder()
                .message("teacher updated")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.teacherResponseFromTeacher(savedData)).build();
    }

    private boolean checkParameterForUpdateMethod(Teacher teacher, TeacherRequest newTeacherRequest) {
        return teacher.getSsn().equalsIgnoreCase(newTeacherRequest.getSsn())
                || teacher.getUsername().equalsIgnoreCase(newTeacherRequest.getUsername())
                || teacher.getPhoneNumber().equalsIgnoreCase(newTeacherRequest.getPhoneNumber())
                || teacher.getEmail().equalsIgnoreCase(newTeacherRequest.getEmail());
    }

    public List<TeacherResponse> getTeacherByName(String teacherName) {
        return teacherRepository.getTeacherByNameContaining(teacherName).stream()
                .map(Mapper::teacherResponseFromTeacher).collect(Collectors.toList());
    }

    public ResponseMessage deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id))
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);

        teacherRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Teacher is Deleted")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<TeacherResponse> getSavedTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        return ResponseMessage.<TeacherResponse>builder()
                .message("teacher found")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.teacherResponseFromTeacher(teacher)).build();
    }

    public Page<TeacherResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return teacherRepository.findAll(pageable).map(Mapper::teacherResponseFromTeacher);
    }

    public ResponseMessage<TeacherResponse> chooseLesson(ChooseLessonTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(request.getTeacherId()).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        Set<LessonProgram> lPrograms = lessonProgramService.getLessonProgramById(request.getLessonProgramId());

        if (lPrograms.size() == 0)
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);

        Set<LessonProgram> existsLessonProgram = teacher.getLessonsProgramList();

        CheckSameLessonProgram.checkLessonProgram(existsLessonProgram, lPrograms);

        existsLessonProgram.addAll(lPrograms);

        teacher.setLessonsProgramList(existsLessonProgram);

        Teacher savedData = teacherRepository.save(teacher);

        return ResponseMessage.<TeacherResponse>builder()
                .message("Lesson Program added to the teacher.")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.teacherResponseFromTeacher(savedData)).build();
    }

    //    StudentInfo Service icin eklendi
    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
    }

    public boolean existsByUsername(String username) {
        return teacherRepository.existsByUsername(username);
    }
}
