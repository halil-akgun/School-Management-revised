package com.schoolmanagement.controller;

import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import com.schoolmanagement.service.ViceDeanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("vicedean")
@RequiredArgsConstructor
public class ViceDeanController {

    private final ViceDeanService viceDeanService;

    @PostMapping("/save") // http://localhost:8080/vicedean/save
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> save(@RequestBody @Valid ViceDeanRequest viceDeanRequest) {
        return viceDeanService.save(viceDeanRequest);
    }

    @PutMapping("/update/{id}") // http://localhost:8080/vicedean/update
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseMessage<ViceDeanResponse> update(@RequestBody @Valid ViceDeanRequest request,
                                                    @PathVariable Long id) {
        return viceDeanService.update(request, id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/delete/{id}") // http://localhost:8080/vicedean/delete/1
    public ResponseMessage<?> delete(@PathVariable Long id) {

        return viceDeanService.deleteViceDean(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/getViceDeanById/{id}") // http://localhost:8080/vicedean/getViceDeanById/1
    public ResponseMessage<ViceDeanResponse> getViceDeanById(@PathVariable Long id) {

        return viceDeanService.getViceDeanById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/getAll") // http://localhost:8080/vicedean/getAll
    public List<ViceDeanResponse> getAll() {

        return viceDeanService.getAllViceDean();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/search") // http://localhost:8080/vicedean/getAllWithPage
    public Page<ViceDeanResponse> getAllWithPage(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "sort") String sort,
            @RequestParam(value = "type") String type
    ) {

        return viceDeanService.getAllWithPage(page, size, sort, type);
    }
}
/*
{
  "username": "viceDean1",
  "name": "Vice",
  "surname": "Dean",
  "birthDay": "1990-01-01",
  "ssn": "173-45-6789",
  "birthPlace": "New York",
  "password": "examplePassword",
  "phoneNumber": "103-416-7890",
  "gender": "MALE"
}
 */