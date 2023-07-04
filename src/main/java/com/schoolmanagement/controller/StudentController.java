package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Student;
import com.schoolmanagement.payload.request.ChooseLessonProgramWithId;
import com.schoolmanagement.payload.request.StudentRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.StudentResponse;
import com.schoolmanagement.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<StudentResponse> save(@RequestBody @Valid StudentRequest request) {
        return studentService.save(request);
    }

    @GetMapping("/changeStatus")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> changeStatus(@RequestParam Long id, @RequestParam boolean status) {
//                             @RequestParam'da name veya value zorunlu degil, degisken ismini name kabul eder
        return studentService.changeStatus(id, status);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<StudentResponse> getAll() {
        return studentService.getAll();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<StudentResponse> update(@RequestBody @Valid StudentRequest request,
                                                   @PathVariable Long id) {
        return studentService.update(request, id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long id) {
        return studentService.delete(id);
    }

    @GetMapping("/getStudentByName")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<StudentResponse> getStudentByName(@RequestParam(name = "name") String studentName) {
        return studentService.getStudentByName(studentName);
    }

    @GetMapping("/getStudentById")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Student getStudentById(@RequestParam(name = "id") Long id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<StudentResponse> getAllWithPage(
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size,
            @RequestParam(name = "sort") String sort,
            @RequestParam(name = "type") Sort.Direction type
    ) {
        return studentService.getAllWithPage(page, size, sort, type);
    }

    @PostMapping("/chooseLesson")
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public ResponseMessage<StudentResponse> chooseLesson(HttpServletRequest request,
                                                         @RequestBody @Valid ChooseLessonProgramWithId lessonProgram) {
//        service'de olursa daha iyi olur:
        String username = (String) request.getHeader("username");
        return studentService.chooseLesson(username, lessonProgram);
    }

    @GetMapping("/getAllByAdvisorId")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public List<StudentResponse> getAllByAdvisorId(HttpServletRequest request) {
        String username = (String) request.getHeader("username");
        return studentService.getAllStudentByTeacher_Username(username);
    }
}
/*
{
    "username": "student1",
    "name": "John",
    "surname": "Doe",
    "birthDay": "1990-01-01",
    "ssn": "123-45-6789",
    "birthPlace": "New York",
    "password": "12345678",
    "phoneNumber": "555-123-4567",
    "gender": "MALE",
    "motherName": "Jane",
    "fatherName": "Doe",
    "email": "aaa@example.com",
    "advisorTeacherId": 2
}
 */