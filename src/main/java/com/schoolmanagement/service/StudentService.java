package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.LessonProgram;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.CheckSameLessonProgram;
import com.schoolmanagement.utils.FieldControl;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final FieldControl fieldControl;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;
    private final LessonProgramService lessonProgramService;

    public ResponseMessage<StudentResponse> save(StudentRequest request) {
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(request.getAdvisorTeacherId()).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE,
                        request.getAdvisorTeacherId())));

        fieldControl.checkDuplicate(request.getUsername(), request.getSsn(),
                request.getPhoneNumber(), request.getEmail());

        Student student = createStudent(request);
        student.setStudentNumber(lastNumber());
        student.setAdvisorTeacher(advisorTeacher);
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setActive(true);

        return ResponseMessage.<StudentResponse>builder()
                .message("Student created.")
                .httpStatus(HttpStatus.CREATED)
                .object(createStudentResponse(studentRepository.save(student))).build();
    }

    private Student createStudent(StudentRequest request) {
        return Student.builder()
                .fatherName(request.getFatherName())
                .motherName(request.getMotherName())
                .email(request.getEmail())
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .birthPlace(request.getBirthPlace())
                .ssn(request.getSsn())
                .gender(request.getGender())
                .birthday(request.getBirthDay())
                .phoneNumber(request.getPhoneNumber())
                .userRole(userRoleService.getUserRole(RoleType.STUDENT))
                .build();
    }

    public int lastNumber() {
        if (!studentRepository.findStudent())
            return 1000;
        return studentRepository.getMaxStudentNumber() + 1;
    }

    public StudentResponse createStudentResponse(Student student) {
        return StudentResponse.builder()
                .fatherName(student.getFatherName())
                .motherName(student.getMotherName())
                .email(student.getEmail())
                .name(student.getName())
                .studentNumber(student.getStudentNumber())
                .surname(student.getSurname())
                .username(student.getUsername())
                .birthPlace(student.getBirthPlace())
                .ssn(student.getSsn())
                .gender(student.getGender())
                .birthday(student.getBirthday())
                .phoneNumber(student.getPhoneNumber())
                .userId(student.getId())
                .isActive(student.isActive())
                .build();
    }

    public ResponseMessage<?> changeStatus(Long id, boolean status) {
        Student student = studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        student.setActive(status);
        studentRepository.save(student);

        return ResponseMessage.builder()
                .message("Student is " + (status ? "Active" : "Passive"))
                .httpStatus(HttpStatus.OK).build();
    }

    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream()
                .map(this::createStudentResponse).collect(Collectors.toList());
    }

    public ResponseMessage<StudentResponse> update(StudentRequest request, Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherById(request
                .getAdvisorTeacherId()).orElseThrow(() -> new ResourceNotFoundException(String
                .format(Messages.NOT_FOUND_ADVISOR_MESSAGE, request.getAdvisorTeacherId())));

        if (!CheckParameterUpdateMethod.checkParameter(student, request)) {
            fieldControl.checkDuplicate(request.getUsername(), request.getSsn(),
                    request.getPhoneNumber(), request.getEmail());
        }

        Student updatedStudent = createStudent(request);
        updatedStudent.setId(id);
        updatedStudent.setPassword(request.getPassword());
        updatedStudent.setAdvisorTeacher(advisorTeacher);
        updatedStudent.setActive(true);
        updatedStudent.setStudentNumber(student.getStudentNumber());

        studentRepository.save(updatedStudent);

        return ResponseMessage.<StudentResponse>builder()
                .message("Student updated")
                .httpStatus(HttpStatus.OK)
                .object(createStudentResponse(updatedStudent)).build();
    }

    public ResponseMessage<?> delete(Long id) {
        if (!studentRepository.existsById(id))
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        studentRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("student deleted")
                .httpStatus(HttpStatus.OK).build();
    }

    public List<StudentResponse> getStudentByName(String studentName) {
        return studentRepository.getStudentByNameContaining(studentName).stream()
                .map(this::createStudentResponse).collect(Collectors.toList());
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
    }

    public Page<StudentResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {
//        Pageable pageable;
//        if (Objects.equals(type, "desc"))
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort));

        return studentRepository.findAll(pageable).map(this::createStudentResponse);
    }

    public ResponseMessage<StudentResponse> chooseLesson(String username, ChooseLessonProgramWithId lessonProgram) {
        Student student = studentRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));
        Set<LessonProgram> lessonPrograms =
                lessonProgramService.getLessonProgramById(lessonProgram.getLessonProgramId());
        if (lessonPrograms.isEmpty())
            throw new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE);

        Set<LessonProgram> oldLessonProgram = student.getLessonsProgramList();

        CheckSameLessonProgram.checkLessonProgram(oldLessonProgram, lessonPrograms);

        oldLessonProgram.addAll(lessonPrograms);

        student.setLessonsProgramList(oldLessonProgram);

        Student savedStudent = studentRepository.save(student);

        return ResponseMessage.<StudentResponse>builder()
                .message("Lessons added to the student")
                .httpStatus(HttpStatus.OK)
                .object(createStudentResponse(savedStudent)).build();
    }

    public List<StudentResponse> getAllStudentByTeacher_Username(String username) {
        return studentRepository.getAllStudentByAdvisorTeacher_Username(username).stream()
                .map(this::createStudentResponse).collect(Collectors.toList());
    }

    public boolean existsByUsername(String username) {
        return studentRepository.existsByUsername(username);
    }

    public boolean existsById(Long id) {
        return studentRepository.existsById(id);
    }

    public List<Student> getStudentByIds(Long[] studentIds) {
        return studentRepository.findByIdsEquals(studentIds);
    }

    public Optional<Student> getStudentByUsername(String username) {
        return studentRepository.findByUsernameEqualsForOptional(username);
    }
}
