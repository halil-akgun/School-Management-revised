package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.EducationTerm;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.EducationTermRequest;
import com.schoolmanagement.payload.response.EducationTermResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.EducationTermRepository;
import com.schoolmanagement.utils.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {

    private final EducationTermRepository educationTermRepository;

    // Not :  Save() *************************************************************************
    public ResponseMessage<EducationTermResponse> save(EducationTermRequest request) {

        //!!! son kayiot tarihi , ders doneminin baslangic tarihinde nsonra olmamali :
        if (request.getLastRegistrationDate().isAfter(request.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
        }

        //!!! bitis tarigi baslangic tarihinden once olmamali
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_LAST_START_DATE);
        }

        // !!! ayni term ve baslangic tarihine sahip birden fazla kayit var mi kontrolu
        if (educationTermRepository.existsByTermAndYear(request.getTerm(), request.getStartDate().getYear())) {
            throw new ResourceNotFoundException(Messages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
        }

        // !!! save metoduna dto- pojo donusumu yapip gonderiyoruz
        EducationTerm savedEducationTerm = educationTermRepository.save(createEducationTerm(request));

        // !!! response objesi olusturuluyor
        return ResponseMessage.<EducationTermResponse>builder()
                .message("Education Term created")
                .object(createEducationTermResponse(savedEducationTerm))
                .httpStatus(HttpStatus.CREATED)
                .build();
    }

    private EducationTerm createEducationTerm(EducationTermRequest request) {

        return EducationTerm.builder()
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .build();
    }

    private EducationTermResponse createEducationTermResponse(EducationTerm response) {

        return EducationTermResponse.builder()
                .id(response.getId())
                .term(response.getTerm())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .lastRegistrationDate(response.getLastRegistrationDate())
                .build();
    }

    public EducationTermResponse get(Long id) {

        if (!educationTermRepository.existsByIdEquals(id))
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
//        checkEducationTermExists(id);

        return createEducationTermResponse(educationTermRepository.findByIdEquals(id));
    }

    public List<EducationTermResponse> getAll() {

        return educationTermRepository.findAll()
                .stream().map(this::createEducationTermResponse).collect(Collectors.toList());
    }

    public Page<EducationTermResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return educationTermRepository.findAll(pageable).map(this::createEducationTermResponse);
    }

    public ResponseMessage<?> delete(Long id) {

//        if (!educationTermRepository.existsById(id)) // created method
//            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
        checkEducationTermExists(id);

        educationTermRepository.deleteById(id);

        return ResponseMessage.builder()
                .message("educationTerm deleted")
                .httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<EducationTermResponse> update(Long id, EducationTermRequest request) {

        // !!! id kontrolu
//        if (!educationTermRepository.existsById(id)) // created method
//            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
        checkEducationTermExists(id);

        // !!! getStartDate ve lastRegistrationDate kontrolu
        if (request.getStartDate() != null && request.getLastRegistrationDate() != null) {
            if (request.getLastRegistrationDate().isAfter(request.getStartDate())) {
                throw new ResourceNotFoundException(Messages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE);
            }
        }

        // !!! startDate-endDate kontrolu
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (request.getEndDate().isBefore(request.getStartDate())) {
                throw new ResourceNotFoundException(Messages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
            }
        }

        ResponseMessage.ResponseMessageBuilder<EducationTermResponse> responseMessageBuilder =
                ResponseMessage.builder();

        EducationTerm updatedEducationTerm = createUpdatedEducationTerm(id, request);
        educationTermRepository.save(updatedEducationTerm);

        return responseMessageBuilder.object(createEducationTermResponse(updatedEducationTerm))
                .message("EducationTerm updated.")
                .build();
    }

    private EducationTerm createUpdatedEducationTerm(Long id, EducationTermRequest request) {
        return EducationTerm.builder()
                .id(id)
                .term(request.getTerm())
                .startDate(request.getStartDate())
                .lastRegistrationDate(request.getLastRegistrationDate())
                .endDate(request.getEndDate())
                .build();
    }

    public EducationTerm getById(Long id) {
        return educationTermRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id)));
    }

    private void checkEducationTermExists(Long id) {
        if (!educationTermRepository.existsById(id))
            throw new ResourceNotFoundException(String.format(Messages.EDUCATION_TERM_NOT_FOUND_MESSAGE, id));
    }
}