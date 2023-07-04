package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.MeetRequestWithoutId;
import com.schoolmanagement.payload.request.UpdateMeetRequest;
import com.schoolmanagement.payload.response.MeetResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("meet")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseMessage<MeetResponse> save(@RequestBody @Valid MeetRequestWithoutId request,
                                              HttpServletRequest httpServletRequest) {
        String username = (String) httpServletRequest.getHeader("username");
        return meetService.save(username, request);
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<MeetResponse> getAll() {
        return meetService.getAll();
    }

    @GetMapping("/getMeetById/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseMessage<MeetResponse> getMeetById(@PathVariable Long id) {
        return meetService.getMeetById(id);
    }

    @GetMapping("/getAllMeetByAdvisorAsPage")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<Page<MeetResponse>> getAllMeetByAdvisorAsPage(
            HttpServletRequest request,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String username = (String) request.getHeader("username");
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<MeetResponse> meetResponses = meetService.getAllMeetByAdvisorTeacherAsPage(username, pageable);

        return ResponseEntity.ok(meetResponses);
    }

    @GetMapping("/getAllMeetByAdvisorTeacherAsList")
    @PreAuthorize("hasAnyAuthority('TEACHER')")
    public ResponseEntity<List<MeetResponse>> getAllMeetByAdvisorTeacherAsList(HttpServletRequest request) {
        String username = (String) request.getHeader("username");
        List<MeetResponse> meetResponses = meetService.getAllMeetByAdvisorTeacherAsList(username);
        return ResponseEntity.ok(meetResponses);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage<?> delete(@PathVariable Long id) {
        return meetService.delete(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
    public ResponseMessage<MeetResponse> update(@RequestBody @Valid UpdateMeetRequest request,
                                                @PathVariable Long id) {
        return meetService.update(request, id);
    }

    @GetMapping("/getAllMeetByStudent")
    @PreAuthorize("hasAnyAuthority('STUDENT')")
    public List<MeetResponse> getAllMeetByStudent(HttpServletRequest request) {
        String username = (String) request.getHeader("username");
        return meetService.getAllMeetByStudentByUsername(username);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<MeetResponse> getAllMeetWithPage(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return meetService.getAllMeetWithPage(page, size);
    }
}
/*
{
    "description": "Toplanti aciklamasi",
    "date": "2023-12-21",
    "startTime": "12:30",
    "stopTime": "13:30",
    "studentIds": [1]
}
 */