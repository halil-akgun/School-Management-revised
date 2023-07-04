package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.ChooseLessonTeacherRequest;
import com.schoolmanagement.payload.request.TeacherRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.TeacherResponse;
import com.schoolmanagement.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping("/save") // http://localhost:8080/teachers/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> save(@Valid @RequestBody TeacherRequest request) {
        return teacherService.save(request);
    }

    @GetMapping("/getAll") // http://localhost:8080/teachers/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<TeacherResponse> getAllTeacher() {
        return teacherService.getAllTeacher();
    }

    @PutMapping("/update/{id}") // http://localhost:8080/teachers/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> update(@PathVariable Long id,
                                                   @RequestBody @Valid TeacherRequest request) {
        return teacherService.update(id, request);
    }

    @GetMapping("/getTeacherByName") // http://localhost:8080/teachers/getTeacherByName
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<TeacherResponse> getTeacherByName(@RequestParam(name = "name") String teacherName) {
        return teacherService.getTeacherByName(teacherName);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage deleteTeacher(@PathVariable Long id) {
        return teacherService.deleteTeacher(id);
    }

    @GetMapping("/getSavedTeacherById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> getSavedTeacherById(@PathVariable Long id) {
        return teacherService.getSavedTeacherById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<TeacherResponse> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ) {
        return teacherService.getAllWithPage(page, size, sort, type);
    }

    // Not: addLessonProgramToTeachersLessonsProgram() **********************************
    @PostMapping("/chooseLesson")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<TeacherResponse> chooseLesson(@RequestBody @Valid ChooseLessonTeacherRequest request) {
        return teacherService.chooseLesson(request);
    }
}
/*
{
    "username": "teacher1",
    "name": "teacher",
    "surname": "teacher",
    "birthDay": "1975-08-15",
    "ssn": "123-12-4289",
    "birthPlace": "US",
    "password": "12345678",
    "phoneNumber": "155-003-4527",
    "gender": "MALE",
    "lessonsIdList": [1],
    "isAdvisorTeacher": true,
    "email": "vvv@vvv.com"
}
 */