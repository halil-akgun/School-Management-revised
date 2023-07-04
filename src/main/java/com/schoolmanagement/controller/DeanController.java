package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.service.DeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("dean")
@RequiredArgsConstructor
public class DeanController {

    private final DeanService deanService;

    // ************************  save  ************************
    @PostMapping("/save") // http://localhost:8080/dean/save
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> save(@Valid @RequestBody DeanRequest deanRequest) {

        return deanService.save(deanRequest);
    }


    // *********************  updateById  *********************
    @PutMapping("/update/{userId}") // http://localhost:8080/dean/update/1
// PutMapping-PatchMapping farki: put ile komple bir degisiklik olur, patch ile sadece degisen field'lar guncellenir
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> update(@RequestBody @Valid DeanRequest deanRequest,
                                                @PathVariable Long userId) {
        return deanService.update(deanRequest, userId);
    }

    // *********************  delete  *********************
    @DeleteMapping("/delete/{userId}") // http://localhost:8080/dean/delete/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> delete(@PathVariable Long userId) {

        return deanService.delete(userId);
    }

    // *********************  getById  *********************
    @GetMapping("/getManagerById/{userId}") // http://localhost:8080/dean/getManagerById/1
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseMessage<DeanResponse> getManagerById(@PathVariable Long userId) {

        return deanService.getDeanById(userId);
    }

    // *********************  getAll  *********************
    @GetMapping("/getAll") // http://localhost:8080/dean/getAll
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<DeanResponse> getAll() {

        return deanService.getAllDean();
    }

    // *********************  search  *********************
    @GetMapping("/search") // http://localhost:8080/dean/search
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<DeanResponse> search(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ) {
        return deanService.search(page, size, sort, type);
    }

}
/*
{
  "username": "johnsmith33",
  "name": "John",
  "surname": "Smith",
  "birthDay": "1990-01-01",
  "ssn": "323-45-6780",
  "birthPlace": "New York",
  "password": "password123",
  "phoneNumber": "323-056-7890",
  "gender": "MALE"
}
 */