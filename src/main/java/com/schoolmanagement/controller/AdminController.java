package com.schoolmanagement.controller;

import com.schoolmanagement.entity.concretes.Admin;
import com.schoolmanagement.payload.request.AdminRequest;
import com.schoolmanagement.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ********************** save() **********************
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')") // tek oldugu icin 'hasRole' ve 'hasAuthority' de kullanilabilir
    public ResponseEntity<?> save(@RequestBody @Valid AdminRequest adminRequest) {

        return ResponseEntity.ok(adminService.save(adminRequest));

    }

    // ********************** getALL() **********************
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<Admin>> getAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "type", defaultValue = "desc") String type
    ) {
        Pageable pageable;

        if (Objects.equals(type, "desc")) {
            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        Page<Admin> author = adminService.getAllAdmin(pageable);

        return new ResponseEntity<>(author, HttpStatus.OK);
    }


    // ********************** delete() **********************
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.deleteAdmin(id));
    }
}
/*
{
  "username": "john_doe",
  "name": "John",
  "surname": "Doe123",
  "birthDay": "1990-01-01",
  "ssn": "123-45-6789",
  "birthPlace": "New York",
  "password": "password123",
  "phoneNumber": "555-123-4567",
  "gender": "MALE",
  "built_in" : false
}
 */