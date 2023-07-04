package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.AdvisorTeacher;
import com.schoolmanagement.entity.concretes.Meet;
import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.MeetRequestWithoutId;
import com.schoolmanagement.payload.request.UpdateMeetRequest;
import com.schoolmanagement.payload.response.MeetResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.MeetRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.utils.Messages;
import com.schoolmanagement.utils.TimeControl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final MeetRepository meetRepository;
    private final AdvisorTeacherService advisorTeacherService;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public ResponseMessage<MeetResponse> save(String username, MeetRequestWithoutId request) {
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, username)));

//        control meet time
        if (TimeControl.check(request.getStartTime(), request.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

//        meet conflict control for each students
        List<Long> studentIds = new ArrayList<>(); // _zZ ***+*** ADDED ***
        for (Long studentId : request.getStudentIds()) {
            if (!studentRepository.existsById(studentId))
                throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, studentId));

            List<Meet> meets = meetRepository.findByStudentList_IdEquals(studentId); // _zZ ***+*** ADDED ***
            try {  // _zZ ***+*** ADDED try/catch ***
                checkMeetConflict(meets, request.getDate(), request.getStartTime(), request.getStopTime());
            } catch (ConflictException e) {
                studentIds.add(studentId);
            }
        }
        if (!studentIds.isEmpty()) // _zZ ***+*** ADDED ***
            throw new ConflictException(String.format(Messages.MEET_CONFLICT_MESSAGE_WITH_ID, studentIds));

//        meet conflict control for teacher
        List<Meet> meets = meetRepository.getByAdvisorTeacher_IdEquals(advisorTeacher.getId()); // _zZ ***+*** ADDED ***
        checkMeetConflict(meets, request.getDate(), request.getStartTime(), request.getStopTime()); // _zZ ***+*** ADDED ***

        List<Student> students = studentService.getStudentByIds(request.getStudentIds());

        Meet meet = new Meet();
        meet.setDate(request.getDate());
        meet.setStartTime(request.getStartTime());
        meet.setStopTime(request.getStopTime());
        meet.setStudentList(students);
        meet.setDescription(request.getDescription());
        meet.setAdvisorTeacher(advisorTeacher);

        Meet savedMeet = meetRepository.save(meet);

        return ResponseMessage.<MeetResponse>builder()
                .message("Meet saved.")
                .httpStatus(HttpStatus.CREATED)
                .object(createMeetResponse(savedMeet)).build();
    }

    private void checkMeetConflict(List<Meet> meets, LocalDate date, LocalTime startTime, LocalTime stopTime) {
//        List<Meet> meets = meetRepository.findByStudentList_IdEquals(studentId); // _zZ ***+*** CANCELED ***
        for (Meet meet : meets) {
            LocalTime existingStartTime = meet.getStartTime();
            LocalTime existingStopTime = meet.getStopTime();

            if (meet.getDate().equals(date) &&
                    ((startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
                            (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
                            (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
                            (startTime.equals(existingStartTime) && stopTime.equals(existingStopTime))))
                throw new ConflictException(Messages.MEET_CONFLICT_MESSAGE);
        }
    }

    private MeetResponse createMeetResponse(Meet meet) {
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

    public List<MeetResponse> getAll() {
        return meetRepository.findAll().stream().map(this::createMeetResponse).collect(Collectors.toList());
    }

    public ResponseMessage<MeetResponse> getMeetById(Long id) {
        Meet meet = meetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE, id)));
        return ResponseMessage.<MeetResponse>builder()
                .message("Meet found")
                .httpStatus(HttpStatus.OK)
                .object(createMeetResponse(meet)).build();
    }

    public Page<MeetResponse> getAllMeetByAdvisorTeacherAsPage(String username, Pageable pageable) {
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, username)));

        return meetRepository.findByAdvisorTeacher_IdEquals(advisorTeacher.getId(), pageable)
                .map(this::createMeetResponse);
    }

    public List<MeetResponse> getAllMeetByAdvisorTeacherAsList(String username) {
        AdvisorTeacher advisorTeacher = advisorTeacherService.getAdvisorTeacherByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_ADVISOR_MESSAGE_WITH_USERNAME, username)));

        return meetRepository.getByAdvisorTeacher_IdEquals(advisorTeacher.getId()).stream()
                .map(this::createMeetResponse).collect(Collectors.toList());
    }

    public ResponseMessage<?> delete(Long id) {
        meetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE, id)));

        meetRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Meet deleted.")
                .httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<MeetResponse> update(UpdateMeetRequest request, Long id) {
        Meet oldMeet = meetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.MEET_NOT_FOUND_MESSAGE, id)));

        if (TimeControl.check(request.getStartTime(), request.getStopTime()))
            throw new BadRequestException(Messages.TIME_NOT_VALID_MESSAGE);

        // !!! If the date, startTime and stoptime information of the original meet object has been changed
        // with the meet from the request, it is entered into the checkMeetConflict method.
        if (!(oldMeet.getDate().equals(request.getDate()) &&
                oldMeet.getStartTime().equals(request.getStartTime()) &&
                oldMeet.getStopTime().equals(request.getStopTime()))) {
//        meet conflict control for each student
            List<Long> studentIds = new ArrayList<>(); // _zZ ***+*** ADDED ***
            for (Long studentId : request.getStudentIds()) {
                List<Meet> meets = meetRepository.findByStudentList_IdEquals(studentId); // _zZ ***+*** ADDED ***
                try {  // _zZ ***+*** ADDED try/catch ***
                    checkMeetConflict(meets, request.getDate(), request.getStartTime(), request.getStopTime());
                } catch (ConflictException e) {
                    studentIds.add(studentId);
                }
            }
            if (!studentIds.isEmpty()) // _zZ ***+*** ADDED ***
                throw new ConflictException(String.format(Messages.MEET_CONFLICT_MESSAGE_WITH_ID, studentIds));

//        meet conflict control for teacher
            List<Meet> meets = meetRepository.getByAdvisorTeacher_IdEquals(oldMeet.getAdvisorTeacher().getId()); // _zZ ***+*** ADDED ***
            checkMeetConflict(meets, request.getDate(), request.getStartTime(), request.getStopTime()); // _zZ ***+*** ADDED ***
        }

        List<Student> students = studentService.getStudentByIds(request.getStudentIds());

//        DTO --> POJO
        Meet updatedMeet = createUpdatedMeet(request, id);
        updatedMeet.setStudentList(students);
        updatedMeet.setAdvisorTeacher(oldMeet.getAdvisorTeacher());

        Meet savedMeet = meetRepository.save(updatedMeet);

        return ResponseMessage.<MeetResponse>builder()
                .message("Meet updated.")
                .httpStatus(HttpStatus.OK)
                .object(createMeetResponse(savedMeet)).build();
    }

    private Meet createUpdatedMeet(UpdateMeetRequest request, Long id) {
        return Meet.builder()
                .id(id)
                .startTime(request.getStartTime())
                .stopTime(request.getStopTime())
                .date(request.getDate())
                .description(request.getDescription())
                .build();
    }

    public List<MeetResponse> getAllMeetByStudentByUsername(String username) {
        Student student = studentService.getStudentByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException(Messages.NOT_FOUND_USER_MESSAGE));

        return meetRepository.findByStudentList_IdEquals(student.getId()).stream()
                .map(this::createMeetResponse).collect(Collectors.toList());
    }

    public Page<MeetResponse> getAllMeetWithPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return meetRepository.findAll(pageable).map(this::createMeetResponse);
    }
}
