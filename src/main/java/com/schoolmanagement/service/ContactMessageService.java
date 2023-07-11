package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.ContactMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.ContactMessageRepository;
import com.schoolmanagement.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor // final fieldlardan constructor olusturuyor
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;


    // ********************** save() **********************
    public ResponseMessage<ContactMessageResponse> save(ContactMessageRequest request) {

        // ayni kisi ayni gun icinde sadece sadece bir defa mesaj gonderebilmeli
//        boolean isSameMessageWithSameEmailForToday =
//                contactMessageRepository.existsByEmailEqualsAndDateEquals(request.getEmail(), LocalDate.now());
//  gunluk mesaj siniri iptal
//        if (isSameMessageWithSameEmailForToday)
//            throw new ConflictException(String.format(ALREADY_SENT_A_MESSAGE_TODAY));

        //   DTO  -->  POJO
        ContactMessage contactMessage = Mapper.contactMessageFromContactMessageRequest(request);
        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact message created successfully.")
                .httpStatus(HttpStatus.CREATED)
                .object(Mapper.contactMessageResponseFromContactMessage(savedData))
                .build();

    }

    // ********************** getAll() **********************
    public Page<ContactMessageResponse> getAll(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

        // _zZ ***+*** CANCELED *** :
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending()); // ascending: default
////                                   type'i default olarak ascending yaptik if'te desc mi diye kontrol edilecek
//
//        if (Objects.equals(type, "desc")) {
////            type "desc" ise pageable asagidaki gibi guncellenecek
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        }

//        return contactMessageRepository.findAll(pageable).map(t-> createResponse(t));
        return contactMessageRepository.findAll(pageable).map(Mapper::contactMessageResponseFromContactMessage); // kisa hali
    }

    // ********************** searchByEmail() **********************
    public Page<ContactMessageResponse> searchByEmail(String email, int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

        // _zZ ***+*** CANCELED *** :
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
//
//        if (Objects.equals(type, "desc")) {
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        }

        return contactMessageRepository.findByEmailEquals(email, pageable).map(Mapper::contactMessageResponseFromContactMessage);
    }

    // ********************** searchBySubject() **********************
    public Page<ContactMessageResponse> searchBySubject(String subject, int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

        // _zZ ***+*** CANCELED *** :
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).ascending());
//
//        if (Objects.equals(type, "desc")) {
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        }

        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(Mapper::contactMessageResponseFromContactMessage);
//        contactMessageRepository.findBySubjectLikeIgnoreCase("%" + subject + "%", pageable)
    }
}
