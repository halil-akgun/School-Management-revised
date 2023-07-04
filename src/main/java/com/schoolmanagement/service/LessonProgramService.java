package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.LessonProgramDto;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.payload.request.LessonProgramRequestForUpdate;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.repository.LessonProgramRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.CreateResponseObjectForService;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

    private final LessonProgramRepository lessonProgramRepository;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;
    private final LessonProgramDto lessonProgramDto;
    private final CreateResponseObjectForService createResponseObjectForService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    public ResponseMessage<LessonProgramResponse> save(LessonProgramRequest request) {

//        Lesson Programda olacak dersleri LessonService uzerinden getiroyoruz
        Set<Lesson> lessons = lessonService.getLessonByIdList(request.getLessonIdList())
                .stream().map(lessonService::createLessonFromLessonResponse).collect(Collectors.toSet());

//        EducationTerm id ile getiriliyor
        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());

//        yukarida gelen lessons ici bos degilse zaman kontrolu
        if (lessons.size() == 0)
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        else if (TimeControl.check(request.getStartTime(), request.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

        LessonProgram lessonProgram = createLessonProgramFromRequest(request, lessons);

//        LessonProgram'da EducationTerm setleniyor
        lessonProgram.setEducationTerm(educationTerm);

        LessonProgram savedData = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse>builder()
                .message("LessonProgram created")
                .httpStatus(HttpStatus.CREATED)
                .object(createLessonProgramResponseForSave(savedData)).build();
    }

    private LessonProgram createLessonProgramFromRequest(LessonProgramRequest request, Set<Lesson> lessons) {
        return lessonProgramDto.createLessonProgramFromRequest(request, lessons);
    }

    private LessonProgramResponse createLessonProgramResponseForSave(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .build();
    }

    public List<LessonProgramResponse> getAll() {
        return lessonProgramRepository.findAll().stream()
                .map(this::createLessonProgramResponse).collect(Collectors.toList());
    }

    private LessonProgramResponse createLessonProgramResponse(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
//                .lessonName(lessonProgram.getLesson()) // recursive olusuyor
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .teachers(lessonProgram.getTeachers()
                        .stream().map(this::createTeacherResponse).collect(Collectors.toSet()))
                .students(lessonProgram.getStudents().stream()
                        .map(createResponseObjectForService::createStudentResponse).collect(Collectors.toSet()))
                .build();
    }

    public TeacherResponse createTeacherResponse(Teacher teacher) {
        return TeacherResponse.builder()
                .ssn(teacher.getSsn())
                .userId(teacher.getId())
                .name(teacher.getName())
                .surname(teacher.getSurname())
                .username(teacher.getUsername())
                .birthPlace(teacher.getBirthPlace())
                .phoneNumber(teacher.getPhoneNumber())
                .email(teacher.getEmail())
                .birthday(teacher.getBirthday())
                .gender(teacher.getGender())
                .build();
    }

    public LessonProgramResponse getById(Long id) {
        LessonProgram lessonProgram = lessonProgramRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, id)));

        return createLessonProgramResponse(lessonProgram);
    }

    public List<LessonProgramResponse> getAllUnassigned() {

//   zz             findByTeachers_IdNull: turetilen method (teacherId null olanlar..)
        return lessonProgramRepository.findByTeachers_IdNull().stream()
                .map(this::createLessonProgramResponse).collect(Collectors.toList());
    }

    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramRepository.findByTeachers_IdNotNull().stream()
                .map(this::createLessonProgramResponse).collect(Collectors.toList());
    }

    public ResponseMessage delete(Long id) {
        lessonProgramRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, id)));
        lessonProgramRepository.deleteById(id);


//         bu lessonPrograma dahil olan teacher ve student lardada degisiklik yapilmasi gerekiyor,
//         biz bunu lessonProgram entity sinifi icinde @PreRemove ile yaptik

        return ResponseMessage.builder()
                .message("LessonProgram Deleted.")
                .httpStatus(HttpStatus.OK).build();
    }

    public Set<LessonProgramResponse> getAllLessonProgramByTeacher(String username) {

        return lessonProgramRepository.getLessonProgramByTeacherUsername(username).stream()
                .map(this::createLessonProgramResponseForTeacher).collect(Collectors.toSet());
    }

    private LessonProgramResponse createLessonProgramResponseForTeacher(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .students(lessonProgram.getStudents().stream()
                        .map(createResponseObjectForService::createStudentResponse).collect(Collectors.toSet()))
                .build();
    }

    public Set<LessonProgramResponse> getAllLessonProgramByStudent(String username) {
        return lessonProgramRepository.getLessonProgramByStudentUsername(username).stream()
                .map(this::createLessonProgramResponseForStudent).collect(Collectors.toSet());
    }

    private LessonProgramResponse createLessonProgramResponseForStudent(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .teachers(lessonProgram.getTeachers().stream()
                        .map(this::createTeacherResponse).collect(Collectors.toSet()))
                .build();
    }

    public Page<LessonProgramResponse> getAllWithPage(int page, int size, String sort, String type) {
        Pageable pageable;
        if (Objects.equals(type, "desc"))
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return lessonProgramRepository.findAll(pageable).map(this::createLessonProgramResponse);
    }

    public Set<LessonProgram> getLessonProgramById(Set<Long> lessonIdList) {
        return lessonProgramRepository.getLessonProgramByLessonProgramIdList(lessonIdList);
    }

    public ResponseMessage<LessonProgramResponse> update(Long id, LessonProgramRequestForUpdate request) {

        LessonProgram lessonProgram = lessonProgramRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Messages.LESSON_PROGRAM_NOT_FOUND_MESSAGE));

        Set<Lesson> lessons = lessonService.getLessonByIdList(request.getLessonIdList());

        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());

        //        yukarida gelen lessons ici bos degilse zaman kontrolu
        if (lessons == null || lessons.size() == 0)
            throw new ResourceNotFoundException(Messages.NOT_FOUND_LESSON_IN_LIST);
        else if (TimeControl.check(request.getStartTime(), request.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

//        ogrenci bilgilerini guncelleme
        if (request.getStudentIdList() != null && !request.getStudentIdList().isEmpty()) {
            Set<Student> students = studentRepository.findByIdsEquals(request.getStudentIdList());
            lessonProgram.setStudents(students);
        }

//        ogretmen bilgilerini guncelleme
        if (request.getTeacherIdList() != null && !request.getTeacherIdList().isEmpty()) {
            Set<Teacher> teachers = teacherRepository.findByIdsEquals(request.getTeacherIdList());
            lessonProgram.setTeachers(teachers);
        }

        lessonProgram.setLesson(lessons);
        lessonProgram.setDay(request.getDay());
        lessonProgram.setEducationTerm(educationTerm);
        lessonProgram.setStartTime(request.getStartTime());
        lessonProgram.setStopTime(request.getStopTime());

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        return ResponseMessage.<LessonProgramResponse>builder()
                .message("LessonProgram updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createLessonProgramResponse(savedLessonProgram))
                .build();
    }
}
