package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.repository.StudentInfoRepository;
import com.schoolmanagement.utils.Mapper;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

    private final StudentInfoRepository studentInfoRepository;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final LessonService lessonService;
    private final EducationTermService educationTermService;

    @Value("${midterm.exam.impact.percentage}")
    private double midtermExamPercentage;

    @Value("${final.exam.impact.percentage}")
    private double finalExamPercentage;

    public ResponseMessage<StudentInfoResponse> save(String username, StudentInfoRequestWithoutTeacherId request) {
        Student student = studentService.getStudentById(request.getStudentId());
        Teacher teacher = teacherService.getTeacherByUsername(username);
        Lesson lesson = lessonService.getLessonById(request.getLessonId());
        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());

//        lesson cakisma var mi kontrolu
        if (checkSameLesson(request.getStudentId(), lesson.getLessonName()))
            throw new ConflictException(String.format(Messages.ALREADY_REGISTER_LESSON_MESSAGE, lesson.getLessonName()));

//        Ders notu ortalamasi
        double noteAverage = calculateExamAverage(request.getMidTermExam(), request.getFinalExam());

//        ders notu alfabetik olarak hesaplaniyor
        Note note = checkLetterGrade(noteAverage);

//        DTO --> POJO
        StudentInfo studentInfo = Mapper.studentInfoFromStudentInfoRequestWithoutTeacherId(request, note, noteAverage);
//        requestte sadece id'ler vardi, yukarida elde ettigimiz objeleri setliyoruz:
        studentInfo.setStudent(student);
        studentInfo.setTeacher(teacher);
        studentInfo.setLesson(lesson);
        studentInfo.setEducationTerm(educationTerm);

        StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message("StudentInfo saved.")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.studentInfoResponseFromStudentInfo(savedStudentInfo)).build();
    }

    private boolean checkSameLesson(Long studentId, String lessonName) {
        return studentInfoRepository.getAllByStudentId_Id(studentId).stream()
                .anyMatch(t -> t.getLesson().getLessonName().equalsIgnoreCase(lessonName));
    }

    private Double calculateExamAverage(Double midTermExam, Double finalExam) {
        return (midTermExam * midtermExamPercentage) + (finalExam * finalExamPercentage);
    }

    private Note checkLetterGrade(double average) {

        if (average < 50) return Note.FF;
        else if (average < 55) return Note.DD;
        else if (average < 60) return Note.DC;
        else if (average < 65) return Note.CC;
        else if (average < 70) return Note.CB;
        else if (average < 75) return Note.BB;
        else if (average < 80) return Note.BA;
        else return Note.AA;
    }

    public ResponseMessage<?> delete(Long id) {

        if (!studentInfoRepository.existsByIdEquals(id)) {
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND, id));
        }

        studentInfoRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Student Info deleted successfully")
                .httpStatus(HttpStatus.OK)
                .build();
    }

    public ResponseMessage<StudentInfoResponse> update(UpdateStudentInfoRequest request, Long id) {

        Lesson lesson = lessonService.getLessonById(request.getLessonId());
        StudentInfo oldStudentInfo = getStudentInfoById(id);
        EducationTerm educationTerm = educationTermService.getById(request.getEducationTermId());

//        not ortalamasi hesaplaniyor:
        double noteAverage = calculateExamAverage(request.getMidTermExam(), request.getFinalExam());
//        alfabetik not:
        Note note = checkLetterGrade(noteAverage);

        StudentInfo studentInfo = Mapper.studentInfoFromUpdateStudentInfoRequest(request, id, lesson, educationTerm, note, noteAverage);
        studentInfo.setStudent(oldStudentInfo.getStudent());
        studentInfo.setTeacher(oldStudentInfo.getTeacher());

        StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfo);

        return ResponseMessage.<StudentInfoResponse>builder()
                .message("StudentInfo updated")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.studentInfoResponseFromStudentInfo(updatedStudentInfo)).build();
    }

    private StudentInfo getStudentInfoById(Long id) {
        return studentInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND, id)));
    }

    public Page<StudentInfoResponse> getAllForAdmin(Pageable pageable) {
        return studentInfoRepository.findAll(pageable).map(Mapper::studentInfoResponseFromStudentInfo);
    }

    public Page<StudentInfoResponse> getAllForTeacher(Pageable pageable, String username) {
        if (!teacherService.existsByUsername(username))
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        return studentInfoRepository.findByTeacherId_UsernameEquals(username, pageable)
                .map(Mapper::studentInfoResponseFromStudentInfo);
    }


    public Page<StudentInfoResponse> getAllByStudent(String username, Pageable pageable) {
        if (!studentService.existsByUsername(username))
            throw new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE);
        return studentInfoRepository.findByStudentId_UsernameEquals(username, pageable)
                .map(Mapper::studentInfoResponseFromStudentInfo);
    }

    public List<StudentInfoResponse> getStudentInfoByStudentId(Long id) {
        if (!studentService.existsById(id))
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id));

        if (!studentInfoRepository.existsByStudent_IdEquals(id))
            throw new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND_BY_STUDENT_ID, id));

        return studentInfoRepository.findByStudent_IdEquals(id).stream()
                .map(Mapper::studentInfoResponseFromStudentInfo).collect(Collectors.toList());
    }

    public StudentInfoResponse findStudentInfoById(Long id) {
        return Mapper.studentInfoResponseFromStudentInfo(studentInfoRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.STUDENT_INFO_NOT_FOUND, id))));
    }

    public Page<StudentInfoResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort));
        return studentInfoRepository.findAll(pageable).map(Mapper::studentInfoResponseFromStudentInfo);
    }
}
