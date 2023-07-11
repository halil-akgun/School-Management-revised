package com.schoolmanagement.utils;

import com.schoolmanagement.entity.concretes.*;
import com.schoolmanagement.entity.enums.Note;
import com.schoolmanagement.payload.request.*;
import com.schoolmanagement.payload.response.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class Mapper {


    //  _zZ *************************  Admin  *************************
    public static Admin adminFromAdminRequest(AdminRequest request) {
        return Admin.builder()
                .username(request.getUsername())
                .name(request.getName())
                .surname(request.getSurname())
                .birthday(request.getBirthDay())
                .ssn(request.getSsn())
                .birthPlace(request.getBirthPlace())
                .password(request.getPassword())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender()).build();
    }

    public static AdminResponse adminResponseFromAdmin(Admin admin) {
        return AdminResponse.builder()
                .name(admin.getName())
                .surname(admin.getSurname())
                .gender(admin.getGender())
                .userId(admin.getId())
                .username(admin.getUsername())
                .phoneNumber(admin.getPhoneNumber())
                .build();
    }


    //  _zZ ********************  AdvisorTeacher  *********************
    public static AdvisorTeacherResponse advisorTeacherResponseFromAdvisorTeacher(AdvisorTeacher teacher) {
        return AdvisorTeacherResponse.builder()
                .teacherName(teacher.getTeacher().getName())
                .teacherSurName(teacher.getTeacher().getSurname())
                .teacherSsn(teacher.getTeacher().getSsn())
                .advisorTeacherId(teacher.getId())
                .build();
    }


    //  _zZ ********************  ContactMessage  *********************
    public static ContactMessage contactMessageFromContactMessageRequest(ContactMessageRequest contactMessageRequest) {
        return ContactMessage.builder()
                .name(contactMessageRequest.getName())
                .email(contactMessageRequest.getEmail())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .date(LocalDate.now()).build();
    }

    public static ContactMessageResponse contactMessageResponseFromContactMessage(ContactMessage contactMessage) {
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .subject(contactMessage.getSubject())
                .date(contactMessage.getDate()).build();
    }


    //  _zZ *************************  Dean  **************************
    public static DeanResponse deanResponseFromDean(Dean dean) {
        return DeanResponse.builder()
                .name(dean.getName())
                .surname(dean.getSurname())
                .userId(dean.getId())
                .username(dean.getUsername())
                .ssn(dean.getSsn())
                .birthday(dean.getBirthday())
                .birthPlace(dean.getBirthPlace())
                .phoneNumber(dean.getPhoneNumber())
                .gender(dean.getGender())
                .build();
    }

    public static Dean deanFromDeanRequest(DeanRequest deanRequest, Long deanId) {
        return Dean.builder()
                .id(deanId)
                .username(deanRequest.getUsername())
                .ssn(deanRequest.getSsn())
                .phoneNumber(deanRequest.getPhoneNumber())
                .gender(deanRequest.getGender())
                .birthPlace(deanRequest.getBirthPlace())
                .name(deanRequest.getName())
                .surname(deanRequest.getSurname())
                .birthday(deanRequest.getBirthDay())
                .build();
    }


    //  _zZ *********************  EducationTerm  *********************
    public static EducationTerm educationTermFromEducationTermRequest(EducationTermRequest request) {

        return EducationTerm.builder()
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    public static EducationTermResponse educationTermResponseFromEducationTerm(EducationTerm response) {

        return EducationTermResponse.builder()
                .id(response.getId())
                .term(response.getTerm())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .lastRegistrationDate(response.getLastRegistrationDate())
                .build();
    }

    public static EducationTerm updatedEducationTermFromEducationTermRequest(Long id, EducationTermRequest request) {
        return EducationTerm.builder()
                .id(id)
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .endDate(request.getEndDate())
                .build();
    }


    //  _zZ *********************  LessonProgram  *********************
    public static LessonProgramResponse lessonProgramResponseFromLessonProgram(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .build();
    }

    public static LessonProgramResponse lessonProgramResponseForStudent(LessonProgram lessonProgram) {
        return LessonProgramResponse.builder()
                .day(lessonProgram.getDay())
                .lessonProgramId(lessonProgram.getId())
                .lessonName(lessonProgram.getLesson())
                .startTime(lessonProgram.getStartTime())
                .stopTime(lessonProgram.getStopTime())
                .educationTerm(lessonProgram.getEducationTerm())
                .teachers(lessonProgram.getTeachers().stream()
                        .map(Mapper::teacherResponseFromTeacher).collect(Collectors.toSet()))
                .build();
    }


    //  _zZ *************************  Lesson  ************************
    public static Lesson lessonFromLessonRequest(LessonRequest request) {
        return Lesson.builder()
                .lessonName(request.getLessonName())
                .creditScore(request.getCreditScore())
                .isCompulsory(request.getIsCompulsory()).build();
    }

    public static LessonResponse lessonResponseFromLesson(Lesson lesson) {
        return LessonResponse.builder()
                .lessonName(lesson.getLessonName())
                .lessonId(lesson.getLessonId())
                .creditScore(lesson.getCreditScore())
                .isCompulsory(lesson.getIsCompulsory())
                .build();
    }

    public static Lesson lessonFromLessonResponse(LessonResponse response) {
        return Lesson.builder()
                .isCompulsory(response.isCompulsory())
                .creditScore(response.getCreditScore())
                .lessonName(response.getLessonName())
                .lessonId(response.getLessonId()).build();
    }


    //  _zZ *************************  Meet  **************************
    public static MeetResponse meetResponseFromMeet(Meet meet) {
        return MeetResponse.builder()
                .id(meet.getId())
                .date(meet.getDate())
                .startTime(meet.getStartTime())
                .stopTime(meet.getStopTime())
                .description(meet.getDescription())
                .advisorTeacherId(meet.getAdvisorTeacher().getId())
                .teacherSsn(meet.getAdvisorTeacher().getTeacher().getSsn())
                .teacherName(meet.getAdvisorTeacher().getTeacher().getName())
                .students(meet.getStudentList())
                .build();
    }

    public static Meet meetFromUpdateMeetRequest(UpdateMeetRequest request, Long id) {
        return Meet.builder()
                .id(id)
                .startTime(request.getStartTime())
                .stopTime(request.getStopTime())
                .date(request.getDate())
                .description(request.getDescription())
                .build();
    }


    //  _zZ **********************  StudentInfo  **********************
    public static StudentInfo studentInfoFromStudentInfoRequestWithoutTeacherId(StudentInfoRequestWithoutTeacherId request, Note note, Double average) {
        return StudentInfo.builder()
                .infoNote(request.getInfoNote())
                .absentee(request.getAbsentee())
                .midtermExam(request.getMidTermExam())
                .finalExam(request.getFinalExam())
                .examAverage(average)
                .letterGrade(note)
                .build();
    }

    public static StudentInfoResponse studentInfoResponseFromStudentInfo(StudentInfo studentInfo) {
        return StudentInfoResponse.builder()
                .lessonName(studentInfo.getLesson().getLessonName())
                .creditScore(studentInfo.getLesson().getCreditScore())
                .isCompulsory(studentInfo.getLesson().getIsCompulsory())
                .educationTerm(studentInfo.getEducationTerm().getTerm())
                .id(studentInfo.getId())
                .absentee(studentInfo.getAbsentee())
                .midTermExam(studentInfo.getMidtermExam())
                .finalExam(studentInfo.getFinalExam())
                .infoNote(studentInfo.getInfoNote())
                .note(studentInfo.getLetterGrade())
                .average(studentInfo.getExamAverage())
                .studentResponse(studentResponseFromStudent(studentInfo.getStudent()))
                .build();
    }

    public static StudentInfo studentInfoFromUpdateStudentInfoRequest(UpdateStudentInfoRequest request, Long studentInfoRequestId,
                                                                      Lesson lesson, EducationTerm educationTerm, Note note, double average) {
        return StudentInfo.builder()
                .id(studentInfoRequestId)
                .infoNote(request.getInfoNote())
                .midtermExam(request.getMidTermExam())
                .finalExam(request.getFinalExam())
                .absentee(request.getAbsentee())
                .lesson(lesson)
                .educationTerm(educationTerm)
                .examAverage(average)
                .letterGrade(note)
                .build();
    }


    //  _zZ ************************  Student  ************************
    public static Student studentFromStudentRequest(StudentRequest request) {
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
                .build();
    }

    public static StudentResponse studentResponseFromStudent(Student student) {
        return StudentResponse.builder()
                .ssn(student.getSsn())
                .userId(student.getId())
                .username(student.getUsername())
                .name(student.getName())
                .surname(student.getSurname())
                .birthPlace(student.getBirthPlace())
                .birthday(student.getBirthday())
                .phoneNumber(student.getPhoneNumber())
                .gender(student.getGender())
                .email(student.getEmail())
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .isActive(student.isActive())
                .studentNumber(student.getStudentNumber())
                .build();
    }


    //  _zZ ************************  Teacher  ************************
    public static TeacherResponse teacherResponseFromTeacher(Teacher teacher) {
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

    public static Teacher teacherFromTeacherRequest(TeacherRequest request, Long id) {
        return Teacher.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .ssn(request.getSsn())
                .phoneNumber(request.getPhoneNumber())
                .birthPlace(request.getBirthPlace())
                .birthday(request.getBirthDay())
                .gender(request.getGender())
                .id(id)
                .isAdvisor(request.isAdviser())
                .email(request.getEmail())
                .build();
    }


    //  _zZ ***********************  ViceDean  ************************
    public static ViceDeanResponse viceDeanResponseFromViceDean(ViceDean viceDean) {
        return ViceDeanResponse.builder()
                .name(viceDean.getName())
                .userId(viceDean.getId())
                .username(viceDean.getUsername())
                .surname(viceDean.getSurname())
                .birthPlace(viceDean.getBirthPlace())
                .birthday(viceDean.getBirthday())
                .phoneNumber(viceDean.getPhoneNumber())
                .gender(viceDean.getGender())
                .ssn(viceDean.getSsn())
                .build();
    }

    public static ViceDean viceDeanFromViceDeanRequest(ViceDeanRequest request, Long id) {
        return ViceDean.builder()
                .id(id)
                .name(request.getName())
                .surname(request.getSurname())
                .ssn(request.getSsn())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .birthPlace(request.getBirthPlace())
                .birthday(request.getBirthDay())
                .gender(request.getGender())
                .build();
    }
}
