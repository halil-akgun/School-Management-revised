package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.LessonRequest;
import com.schoolmanagement.payload.response.LessonResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/save") // http://localhost:8080/lessons/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage<LessonResponse> save(@RequestBody @Valid LessonRequest request) {
        return lessonService.save(request);
    }

    @DeleteMapping("/delete/{id}") // http://localhost:8080/lessons/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public ResponseMessage delete(@PathVariable Long id) {
        return lessonService.delete(id);
    }

    @GetMapping("/getLessonByName") // http://localhost:8080/lessons/getLessonByName
    public ResponseMessage<LessonResponse> getLessonByName(@RequestParam String lessonName) {
        return lessonService.getLessonByName(lessonName);
    }

    @GetMapping("/getAll") // http://localhost:8080/lessons/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public List<LessonResponse> getAll() {
        return lessonService.getAll();
    }

    @GetMapping("/search") // http://localhost:8080/lessons/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Page<LessonResponse> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") Sort.Direction type
    ) {
        return lessonService.getAllWithPage(page, size, sort, type);
    }

    @GetMapping("/getAllLessonByLessonId") // http://localhost:8080/lessons/getAllLessonByLessonId
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER')")
    public Set<LessonResponse> getLessonByIdList(@RequestParam(name = "lessonId") Set<Long> idList) {
        return lessonService.getLessonByIdList(idList);
    }
}
/*
{
    "lessonName": "ART",
    "creditScore": 5,
    "isCompulsory": true
}
 */