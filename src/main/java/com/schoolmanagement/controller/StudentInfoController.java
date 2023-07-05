package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.StudentInfoRequestWithoutTeacherId;
import com.schoolmanagement.payload.request.UpdateStudentInfoRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentInfoResponse;
import com.schoolmanagement.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    @PostMapping("/save")
    @PreAuthorize(("hasAnyAuthority('TEACHER')"))
    public ResponseMessage<StudentInfoResponse> save(@RequestBody @Valid StudentInfoRequestWithoutTeacherId request,
                                                     HttpServletRequest httpRequest) {
//        String username = (String) httpRequest.getAttribute("username");
//                      getAttribute takes infos from json, not from header
        String username = httpRequest.getHeader("username");
        return studentInfoService.save(username, request);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize(("hasAnyAuthority('TEACHER','ADMIN')"))
    public ResponseMessage<?> delete(@PathVariable Long id) {
        return studentInfoService.delete(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize(("hasAnyAuthority('TEACHER','ADMIN')"))
    public ResponseMessage<StudentInfoResponse> update(@RequestBody @Valid UpdateStudentInfoRequest request,
                                                       @PathVariable Long id) {
        return studentInfoService.update(request, id);
    }

    @GetMapping("/getAllForAdmin")
    @PreAuthorize(("hasAnyAuthority('ADMIN')"))
    public ResponseEntity<Page<StudentInfoResponse>> getAllForAdmin(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<StudentInfoResponse> studentInfoResponses = studentInfoService.getAllForAdmin(pageable);

        return new ResponseEntity<>(studentInfoResponses, HttpStatus.OK);
    }

    @GetMapping("/getAllForTeacher")
    @PreAuthorize(("hasAnyAuthority('TEACHER')"))
    public ResponseEntity<Page<StudentInfoResponse>> getAllForTeacher(
            HttpServletRequest request,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String username = (String) request.getHeader("username");
        Page<StudentInfoResponse> studentInfoResponses = studentInfoService.getAllForTeacher(pageable, username);

        return new ResponseEntity<>(studentInfoResponses, HttpStatus.OK);
//        return ResponseEntity.ok(studentInfoResponses);
    }

    @GetMapping("/getAllByStudent")
    @PreAuthorize(("hasAnyAuthority('STUDENT')"))
    public ResponseEntity<Page<StudentInfoResponse>> getAllByStudent(
            HttpServletRequest request,
            @RequestParam int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String username = (String) request.getHeader("username");

        Page<StudentInfoResponse> studentInfoResponses = studentInfoService.getAllByStudent(username, pageable);

        return ResponseEntity.ok(studentInfoResponses);
    }

    @GetMapping("/getByStudentId/{id}")
    @PreAuthorize(("hasAnyAuthority('TEACHER','ADMIN','MANAGER','ASSISTANT_MANAGER')"))
    public ResponseEntity<List<StudentInfoResponse>> getByStudentId(@PathVariable Long id) {
        List<StudentInfoResponse> studentInfoResponses = studentInfoService.getStudentInfoByStudentId(id);
        return ResponseEntity.ok(studentInfoResponses);
    }

    @GetMapping("/get/{id}")
    @PreAuthorize(("hasAnyAuthority('TEACHER','ADMIN','MANAGER','ASSISTANT_MANAGER')"))
    public ResponseEntity<StudentInfoResponse> get(@PathVariable Long id) {
        StudentInfoResponse studentInfoResponse = studentInfoService.findStudentInfoById(id);
        return ResponseEntity.ok(studentInfoResponse);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<StudentInfoResponse> getAllWithPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "type", defaultValue = "DESC") Sort.Direction type
    ) {
        return studentInfoService.getAllWithPage(page, size, sort, type);
    }
}
/*
{
    "educationTermId": 1,
    "midtermExam": 85.5,
    "finalExam": 90.0,
    "absentee": 2,
    "infoNote": "This is a sample info note",
    "lessonId": 1,
    "studentId":1
}
 */