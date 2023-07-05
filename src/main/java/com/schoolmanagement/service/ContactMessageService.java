package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.ContactMessage;
import com.schoolmanagement.payload.request.ContactMessageRequest;
import com.schoolmanagement.payload.response.ContactMessageResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.ContactMessageRepository;
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
        ContactMessage contactMessage = createObject(request);
        ContactMessage savedData = contactMessageRepository.save(contactMessage);

        return ResponseMessage.<ContactMessageResponse>builder()
                .message("Contact message created successfully.")
                .httpStatus(HttpStatus.CREATED)
                .object(createResponse(savedData))
                .build();

    }

    private ContactMessage createObject(ContactMessageRequest contactMessageRequest) {
        return ContactMessage.builder()
                .name(contactMessageRequest.getName())
                .email(contactMessageRequest.getEmail())
                .subject(contactMessageRequest.getSubject())
                .message(contactMessageRequest.getMessage())
                .date(LocalDate.now()).build();
    }

    private ContactMessageResponse createResponse(ContactMessage contactMessage) {
        return ContactMessageResponse.builder()
                .name(contactMessage.getName())
                .message(contactMessage.getMessage())
                .email(contactMessage.getEmail())
                .subject(contactMessage.getSubject())
                .date(contactMessage.getDate()).build();
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
        return contactMessageRepository.findAll(pageable).map(this::createResponse); // kisa hali
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

        return contactMessageRepository.findByEmailEquals(email, pageable).map(this::createResponse);
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

        return contactMessageRepository.findBySubjectEquals(subject, pageable).map(this::createResponse);
//        contactMessageRepository.findBySubjectLikeIgnoreCase("%" + subject + "%", pageable)
    }
}
