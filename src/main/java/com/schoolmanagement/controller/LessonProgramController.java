package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.LessonProgramRequest;
import com.schoolmanagement.payload.request.LessonProgramRequestForUpdate;
import com.schoolmanagement.payload.response.LessonProgramResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.LessonProgramService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {

    private final LessonProgramService lessonProgramService;

    @PostMapping("/save") // http://localhost:8080/lessonPrograms/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonProgramResponse> save(@Valid @RequestBody LessonProgramRequest request) {
        return lessonProgramService.save(request);
    }

    @GetMapping("/getAll") // http://localhost:8080/lessonPrograms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAll() {
        return lessonProgramService.getAll();
    }

    @GetMapping("/getById/{id}") // http://localhost:8080/lessonPrograms/getById/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public LessonProgramResponse getById(@PathVariable Long id) {
        return lessonProgramService.getById(id);
    }

    @GetMapping("/getAllUnassigned") // http://localhost:8080/lessonPrograms/getAllUnassigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllUnassigned() {
        return lessonProgramService.getAllUnassigned();
    }

    @GetMapping("/getAllAssigned") // http://localhost:8080/lessonPrograms/getAllAssigned
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public List<LessonProgramResponse> getAllAssigned() {
        return lessonProgramService.getAllAssigned();
    }

    @DeleteMapping("/delete/{id}") // http://localhost:8080/lessonPrograms/delete
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage delete(@PathVariable Long id) {
        return lessonProgramService.delete(id);
    }

    @GetMapping("/getAllLessonProgramByTeacher") // http://localhost:8080/lessonPrograms/getAllLessonProgramByTeacher
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByTeacher(HttpServletRequest request) {
/*
    USERNAME
        1- PathVariable
        2- RequestParam
        3- Authentication - getPrincipal() methodu (bilgisi alinacak kisi login yapmissa olur
           - login yapan admin ise teacher username'e ulasamayiz)
        4- HttpServletRequest
        5- RequestBody (JSON)
    ile alinabilir. (ilk 2 yontem icin endPoint'e ekleme yapmak gerekir)
 */

        String username = (String) request.getHeader("username");

        return lessonProgramService.getAllLessonProgramByTeacher(username);
    }

    @GetMapping("/getAllLessonProgramByStudent") // http://localhost:8080/lessonPrograms/getAllLessonProgramByStudent
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Set<LessonProgramResponse> getAllLessonProgramByStudent(HttpServletRequest request) {
        String username = (String) request.getHeader("username");
        return lessonProgramService.getAllLessonProgramByStudent(username);
    }

    @GetMapping("/search") // http://localhost:8080/lessonPrograms/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER','STUDENT')")
    public Page<LessonProgramResponse> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") Sort.Direction type
    ) {
        return lessonProgramService.getAllWithPage(page, size, sort, type);
    }

    @PutMapping("update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public ResponseMessage<LessonProgramResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid LessonProgramRequestForUpdate request
    ) {
        return lessonProgramService.update(id, request);
    }
}
/*
{
    "day": "MONDAY",
    "startTime": "09:00",
    "stopTime": "11:00",
    "lessonIdList": [1, 2, 3],
    "educationTermId": 1
}
 */