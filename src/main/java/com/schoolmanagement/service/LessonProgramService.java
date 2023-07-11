package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.LessonProgramDto;
import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.payload.request.LessonProgramRequestForUpdate;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.LessonProgramRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.repository.TeacherRepository;
import com.schoolmanagement.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
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
                .stream().map(Mapper::lessonFromLessonResponse).collect(Collectors.toSet());

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
                .object(Mapper.lessonProgramResponseFromLessonProgram(savedData)).build();
    }

    private LessonProgram createLessonProgramFromRequest(LessonProgramRequest request, Set<Lesson> lessons) {
        return lessonProgramDto.createLessonProgramFromRequest(request, lessons);
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
                        .stream().map(Mapper::teacherResponseFromTeacher).collect(Collectors.toSet()))
                .students(lessonProgram.getStudents().stream()
                        .map(createResponseObjectForService::createStudentResponse).collect(Collectors.toSet()))
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
                .map(Mapper::lessonProgramResponseForStudent).collect(Collectors.toSet());
    }


    public Page<LessonProgramResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

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

        Set<LessonProgram> lessonProgramRequest = createSetLessonProgramRequest(request); // for CheckSameLessonProgram
        List<Long> idList = new ArrayList<>(); // _zZ ***+*** ADDED *** - for CheckSameLessonProgram

//        ogrenci bilgilerini guncelleme
        if (request.getStudentIdList() != null && !request.getStudentIdList().isEmpty()) {
            Set<Student> students = studentRepository.findByIdsEquals(request.getStudentIdList());

            for (Student student : students) {  // _zZ ***+*** ADDED ***
                try {
                    Set<LessonProgram> lessonProgramsThatWillNotBeUpdated = student.getLessonsProgramList()
                            .stream().filter(t -> !Objects.equals(t.getId(), id)).collect(Collectors.toSet());
                    CheckSameLessonProgram.checkLessonProgram(lessonProgramsThatWillNotBeUpdated, lessonProgramRequest);
                } catch (BadRequestException e) {
                    idList.add(student.getId());
                }
            }
            if (!idList.isEmpty()) // _zZ ***+*** ADDED ***
                throw new ConflictException(String.format(Messages.LESSON_PROGRAM_CONFLICT_MESSAGE_WITH_ID_FOR_STUDENT, idList));

            lessonProgram.setStudents(students);
        }


//        ogretmen bilgilerini guncelleme
        if (request.getTeacherIdList() != null && !request.getTeacherIdList().isEmpty()) {
            Set<Teacher> teachers = teacherRepository.findByIdsEquals(request.getTeacherIdList());

            for (Teacher teacher : teachers) {  // _zZ ***+*** ADDED ***
                try {
                    Set<LessonProgram> lessonProgramsThatWillNotBeUpdated = teacher.getLessonsProgramList()
                            .stream().filter(t -> !Objects.equals(t.getId(), id)).collect(Collectors.toSet());
                    CheckSameLessonProgram.checkLessonProgram(lessonProgramsThatWillNotBeUpdated, lessonProgramRequest);
                } catch (BadRequestException e) {
                    idList.add(teacher.getId());
                }
            }
            if (!idList.isEmpty()) // _zZ ***+*** ADDED ***
                throw new ConflictException(String.format(Messages.LESSON_PROGRAM_CONFLICT_MESSAGE_WITH_ID_FOR_TEACHER, idList));

            lessonProgram.setTeachers(teachers);
        }

        lessonProgram.setLesson(lessons);
        lessonProgram.setDay(request.getDay());
        lessonProgram.setEducationTerm(educationTerm);
        lessonProgram.setStartTime(request.getStartTime());
        lessonProgram.setStopTime(request.getStopTime());

        LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);

        if (request.getStudentIdList() != null && !request.getStudentIdList().isEmpty()) {
            Set<Student> students = studentRepository.findByIdsEquals(request.getStudentIdList());
            for (Student student : students) {
                updateStudentForLessonProgram(student, savedLessonProgram);
            }
        }


        if (request.getTeacherIdList() != null && !request.getTeacherIdList().isEmpty()) {
            Set<Teacher> teachers = teacherRepository.findByIdsEquals(request.getTeacherIdList());
            for (Teacher teacher : teachers) {
                updateTeacherForLessonProgram(teacher, savedLessonProgram);
            }
        }

        return ResponseMessage.<LessonProgramResponse>builder()
                .message("LessonProgram updated Successfully")
                .httpStatus(HttpStatus.OK)
                .object(createLessonProgramResponse(savedLessonProgram))
                .build();
    }

    private Set<LessonProgram> createSetLessonProgramRequest(LessonProgramRequestForUpdate request) {
        LessonProgram lessonProgram = LessonProgram.builder()
                .day(request.getDay()).startTime(request.getStartTime()).build();
        Set<LessonProgram> set = new HashSet<>();
        set.add(lessonProgram);
        return set;
    }

    void updateStudentForLessonProgram(Student student, LessonProgram lessonProgram) {
        Set<LessonProgram> lessonPrograms = student.getLessonsProgramList();
        if (lessonPrograms.stream().noneMatch(t -> Objects.equals(t.getId(), lessonProgram.getId())))
            lessonPrograms.add(lessonProgram);
        student.setLessonsProgramList(lessonPrograms);
        studentRepository.save(student);
    }

    void updateTeacherForLessonProgram(Teacher teacher, LessonProgram lessonProgram) {
        Set<LessonProgram> lessonPrograms = teacher.getLessonsProgramList();
        if (lessonPrograms.stream().noneMatch(t -> Objects.equals(t.getId(), lessonProgram.getId())))
            lessonPrograms.add(lessonProgram);
        teacher.setLessonsProgramList(lessonPrograms);
        teacherRepository.save(teacher);
    }
}
