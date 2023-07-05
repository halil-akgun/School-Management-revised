package com.schoolmanagement.service;

import com.schoolmanagement.entity.concretes.Lesson;
import com.schoolmanagement.exception.ConflictException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.payload.request.LessonRequest;
import com.schoolmanagement.payload.response.LessonResponse;
import com.schoolmanagement.payload.response.ResponseMessage;
import com.schoolmanagement.repository.LessonRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;

    public ResponseMessage<LessonResponse> save(LessonRequest request) {

        if (existsLessonByLessonName(request.getLessonName()))
            throw new ConflictException(String.format(Messages.
                    ALREADY_REGISTER_LESSON_MESSAGE, request.getLessonName()));

        Lesson lesson = createLesson(request);

        return ResponseMessage.<LessonResponse>builder()
                .object(createLessonResponse(lessonRepository.save(lesson)))
                .message("Lesson created successfully.")
                .httpStatus(HttpStatus.CREATED).build();
    }

    private boolean existsLessonByLessonName(String lessonName) {
        return lessonRepository.existsLessonByLessonNameEqualsIgnoreCase(lessonName);
    }

    private Lesson createLesson(LessonRequest request) {
        return Lesson.builder()
                .lessonName(request.getLessonName())
                .creditScore(request.getCreditScore())
                .isCompulsory(request.getIsCompulsory()).build();
    }

    private LessonResponse createLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .lessonName(lesson.getLessonName())
                .lessonId(lesson.getLessonId())
                .creditScore(lesson.getCreditScore())
                .isCompulsory(lesson.getIsCompulsory())
                .build();
    }

    public ResponseMessage delete(Long id) {

        Lesson lesson = lessonRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, id)));

        lessonRepository.delete(lesson);

        return ResponseMessage.builder()
                .message("lesson deleted.")
                .httpStatus(HttpStatus.OK).build();
    }

    public ResponseMessage<LessonResponse> getLessonByName(String lessonName) {

        Lesson lesson = lessonRepository.getLessonByLessonName(lessonName).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, lessonName)));

        return ResponseMessage.<LessonResponse>builder()
                .message("Lesson found.")
                .object(createLessonResponse(lesson)).build();
    }

    public List<LessonResponse> getAll() {

        return lessonRepository.findAll().stream().map(this::createLessonResponse).collect(Collectors.toList());
    }

    public Page<LessonResponse> getAllWithPage(int page, int size, String sort, Sort.Direction type) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(type, sort)); // _zZ ***+*** ADDED ***

//        if (Objects.equals(type, "desc")) { // _zZ ***+*** CANCELED ***
//            pageable = PageRequest.of(page, size, Sort.by(sort).descending());
//        } else pageable = PageRequest.of(page, size, Sort.by(sort).ascending());

        return lessonRepository.findAll(pageable).map(this::createLessonResponse);
    }

    public Set<LessonResponse> getLessonByIdList(Set<Long> idList) {
        return lessonRepository.getLessonByLessonIdList(idList).stream()
                .map(this::createLessonResponse).collect(Collectors.toSet());
    }

    public Lesson createLessonFromLessonResponse(LessonResponse response) {
        return Lesson.builder()
                .isCompulsory(response.isCompulsory())
                .creditScore(response.getCreditScore())
                .lessonName(response.getLessonName())
                .lessonId(response.getLessonId()).build();
    }

    //      StudentInfoService icin yazildi
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() ->
                new ResourceNotFoundException(String.format(Messages.NOT_FOUND_LESSON_MESSAGE, lessonId)));
    }

    public Set<Lesson> getLessonByIdList(List<Long> idList) {
        return lessonRepository.getLessonByLessonIdList(idList);
    }
}
