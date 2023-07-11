package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.ViceDean;
import com.schoolmanagement.entity.enums.RoleType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.dto.ViceDeanDto;
import com.schoolmanagement.payload.request.ViceDeanRequest;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.payload.response.ViceDeanResponse;
import com.schoolmanagement.repository.ViceDeanRepository;
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
public class ViceDeanService {

    private final ViceDeanRepository viceDeanRepository;
    private final ViceDeanDto viceDeanDto;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleService userRoleService;
    private final FieldControl fieldControl;


    public ResponseMessage<ViceDeanResponse> save(ViceDeanRequest request) {

        fieldControl.checkDuplicate(request.getUsername(), request.getSsn(), request.getPhoneNumber());


        ViceDean viceDean = createPojoFromDto(request);
        viceDean.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
        viceDean.setPassword(passwordEncoder.encode(viceDean.getPassword()));

        viceDeanRepository.save(viceDean);

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean saved.")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.viceDeanResponseFromViceDean(viceDean)).build();

    }

    private ViceDean createPojoFromDto(ViceDeanRequest request) {

        return viceDeanDto.fromDtoToViceDean(request);
    }

    public ResponseMessage<ViceDeanResponse> update(ViceDeanRequest request, Long id) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(id);

        if (viceDean.isEmpty())
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id));
        else if (!CheckParameterUpdateMethod.checkParameter(viceDean.get(), request)) {
            fieldControl.checkDuplicate(request.getUsername(), request.getSsn(), request.getPhoneNumber());
        }
        ViceDean updatedData = Mapper.viceDeanFromViceDeanRequest(request, id);
        updatedData.setPassword(passwordEncoder.encode(request.getPassword()));
        updatedData.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));

        viceDeanRepository.save(updatedData);

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean updated.")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.viceDeanResponseFromViceDean(updatedData))
                .build();

    }

    public ResponseMessage<?> deleteViceDean(Long id) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(id);

        if (viceDean.isEmpty())
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id));

        viceDeanRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("Vice Dean deleted")
                .httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<ViceDeanResponse> getViceDeanById(Long id) {

        Optional<ViceDean> viceDean = viceDeanRepository.findById(id);

        if (viceDean.isEmpty())
            throw new ResourceNotFoundException(String.format(Messages.NOT_FOUND_USER2_MESSAGE, id));

        return ResponseMessage.<ViceDeanResponse>builder()
                .message("ViceDean found")
                .httpStatus(HttpStatus.OK)
                .object(Mapper.viceDeanResponseFromViceDean(viceDean.get())).build();
    }

    public List<ViceDeanResponse> getAllViceDean() {
        return viceDeanRepository.findAll()
                .stream()
                .map(Mapper::viceDeanResponseFromViceDean)
                .collect(Collectors.toList());
    }

    public Page<ViceDeanResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return viceDeanRepository.findAll(pageable).map(Mapper::viceDeanResponseFromViceDean);
    }
}
