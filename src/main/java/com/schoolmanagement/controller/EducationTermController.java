package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("educationTerms")
@RequiredArgsConstructor
public class EducationTermController {

    private final EducationTermService educationTermService;

    @PostMapping("/save") // http://localhost:8080/educationTerms/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> save(@RequestBody EducationTermRequest request) {
        return educationTermService.save(request);
    }

    @GetMapping("/{id}") // http://localhost:8080/educationTerms/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public EducationTermResponse get(@PathVariable Long id) {
        return educationTermService.get(id);
    }

    @GetMapping("/getAll") // http://localhost:8080/educationTerms/getAll
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public List<EducationTermResponse> getAll() {
        return educationTermService.getAll();
    }

    @GetMapping("/search") // http://localhost:8080/educationTerms/search
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASSISTANT_MANAGER','TEACHER')")
    public Page<EducationTermResponse> getAllWithPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "startDate") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        return educationTermService.getAllWithPage(page, size, sort, type);
    }

    @DeleteMapping("/delete/{id}") // http://localhost:8080/educationTerms/delete/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<?> delete(@PathVariable Long id) {
        return educationTermService.delete(id);
    }

    @PutMapping("/update/{id}") // http://localhost:8080/educationTerms/update/1
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<EducationTermResponse> update(@PathVariable Long id,
                                                         @RequestBody @Valid EducationTermRequest request) {
        return educationTermService.update(id, request);
    }
}
/*
{
    "term": "SPRING_SEMESTER",
    "startDate": "2024-05-01",
    "endDate": "2024-06-30",
    "lastRegistrationDate": "2023-12-20"
}
 */