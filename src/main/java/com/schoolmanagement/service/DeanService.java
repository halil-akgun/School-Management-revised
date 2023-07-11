package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Dean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.DeanDto;
import com.schoolmanagement.payload.request.DeanRequest;
import com.schoolmanagement.payload.response.DeanResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.DeanRepository;
import com.schoolmanagement.utils.CheckParameterUpdateMethod;
import com.schoolmanagement.utils.FieldControl;
import com.schoolmanagement.utils.Mapper;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanService {

    private final DeanRepository deanRepository;
    private final DeanDto deanDto;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final FieldControl fieldControl;

    public ResponseMessage<DeanResponse> save(DeanRequest deanRequest) {

//        control duplicate
        fieldControl.checkDuplicate(deanRequest.getUsername(), deanRequest.getSsn(), deanRequest.getPhoneNumber());

//        DTO -> POJO
        Dean dean = createDeanForSave(deanRequest);

        dean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
        dean.setPassword(passwordEncoder.encode(dean.getPassword()));

        Dean savedDean = deanRepository.save(dean);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean saved.")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.deanResponseFromDean(savedDean))
                .build();

    }

    private Dean createDeanForSave(DeanRequest deanRequest) {
        return deanDto.dtoDean(deanRequest);
    }


    public ResponseMessage<DeanResponse> update(DeanRequest request, Long userId) {

//        Optional<Dean> dean = deanRepository.findById(userId);
//        if (!dean.isPresent())  // dean.isEmpty() de kullanilabilir
//            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, userId));
//        else if (!CheckParameterUpdateMethod.checkParameter(dean.get(), request)) {
//        fieldControl.checkDuplicate(request.getUsername(), request.getSsn(), request.getPhoneNumber());
//        }

        Optional<Dean> dean = checkDeanExists(userId);
        if (!CheckParameterUpdateMethod.checkParameter(dean.get(), request)) {
//                                                  Optional<Dean> icinden get() (Dean getir)
            fieldControl.checkDuplicate(request.getUsername(), request.getSsn(), request.getPhoneNumber());
        }


        Dean updatedDean = Mapper.deanFromDeanRequest(request, userId);
        updatedDean.setPassword(passwordEncoder.encode(request.getPassword()));
        updatedDean.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));

        deanRepository.save(updatedDean);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean updated successfully")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.deanResponseFromDean(updatedDean)).build();

    }

    public ResponseMessage<DeanResponse> delete(Long userId) {
//        Optional<Dean> dean = deanRepository.findById(userId);
//        if (dean.isEmpty())
//            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, userId));
        Optional<Dean> dean = checkDeanExists(userId);

        deanRepository.deleteById(userId);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean deleted")
                .httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<DeanResponse> getDeanById(Long userId) {
//        Optional<Dean> dean = deanRepository.findById(userId);
//        if (dean.isEmpty())
//            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, userId));
        Optional<Dean> dean = checkDeanExists(userId);

        return ResponseMessage.<DeanResponse>builder()
                .message("Dean successfully found")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.deanResponseFromDean(dean.get())).build();
    }

    public List<DeanResponse> getAllDean() {
        return deanRepository.findAll().stream().map(Mapper::deanResponseFromDean).collect(Collectors.toList());
    }

    public Page<DeanResponse> search(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return deanRepository.findAll(pageable).map(Mapper::deanResponseFromDean);
    }

    private Optional<Dean> checkDeanExists(Long id) { // to avoid code repeat
        Optional<Dean> dean = deanRepository.findById(id);

        if (dean.isEmpty())
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id));
        return dean;
    }
}
