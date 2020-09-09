package com.studyolle.event;

import com.studyolle.account.CurrentAccount;
import com.studyolle.domain.Account;
import com.studyolle.domain.Enrollment;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import com.studyolle.event.validator.EventValidator;
import com.studyolle.study.StudyRepository;
import com.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController {

    private final StudyService studyService;
    private final EventService eventService;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;

    @InitBinder("eventForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(eventValidator);
    }

    /**
     * 모임 만들기 화면 호출
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/new-event")
    public String newEventForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute(new EventForm());
        return "event/form";
    }

    /**
     * 모임 만들기
     * @param account
     * @param path
     * @param eventForm
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid EventForm eventForm, Errors errors, Model model)  {
        Study study = studyService.getStudyToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    /**
     * 모임 상세조회
     * @param account
     * @param path
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/events/{id}")
    public String getEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id, Model model) {
        model.addAttribute(account);
        model.addAttribute(eventRepository.findById(id).orElseThrow());
        model.addAttribute(studyRepository.findStudyWithManagersByPath(path));
        return "event/view";
    }

    /**
     * 모임 목록 조회
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/events")
    public String viewStudyEvents(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudy(path);
        model.addAttribute(account);
        model.addAttribute(study);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);
        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        events.forEach(event -> {
            if (event.getEndDateTime().isBefore(LocalDateTime.now())) {
                oldEvents.add(event);
            } else {
                newEvents.add(event);
            }
        });

        model.addAttribute("newEvents", newEvents);
        model.addAttribute("oldEvents", oldEvents);

        return "study/events";
    }

    /**
     * 모임 수정 화면 호출
     * @param account
     * @param path
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable Long id, Model model) {

        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        model.addAttribute(study);
        model.addAttribute(event);
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(event, EventForm.class));
        return "event/update-form";
    }

    /**
     * 모임 수정
     * @param account
     * @param path
     * @param id
     * @param eventForm
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/events/{id}/edit")
    public String updateEventForm(@CurrentAccount Account account,
                                  @PathVariable String path, @PathVariable Long id,
                                  @Valid EventForm eventForm, Errors errors, Model model) {
        
        Study study = studyService.getStudyToUpdate(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
        eventForm.setEventType(event.getEventType());
        eventValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute(event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    /**
     * 모임 취소
     * @param account
     * @param path
     * @param id
     * @return
     */
    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path,
                              @PathVariable Long id) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        eventService.deleteEvent(eventRepository.findById(id).orElseThrow());
        return "redirect:/study/" + study.getEncodedPath() + "/events";
    }

    /**
     * 모임참가신청
     * @param account
     * @param path
     * @param event
     * @return
     */
    @PostMapping("/events/{id}/enroll")
    public String newEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                @PathVariable("id") Event event) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.newEnrollment(event, account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    /**
     * 모임참가신청 취소
     * @param account
     * @param path
     * @param id
     * @return
     */
    @PostMapping("/events/{id}/disenroll")
    public String cancelEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                 @PathVariable Long id) {
        Study study = studyService.getStudyToEnroll(path);
        eventService.cancelEnrollment(eventRepository.findById(id).orElseThrow(), account);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + id;
    }

    /**
     * 모임참가신청 승인
     * @param account
     * @param path
     * @param event
     * @param enrollment
     * @return
     */
    @GetMapping("/events/{eventId}/enrollments/{enrollments}/accept")
    public String acceptEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId")Enrollment enrollment) {

        Study study = studyService.getStudyToUpdate(account, path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }

    /**
     * 모임참가신청 승인거절
     * @param account
     * @param path
     * @param event
     * @param enrollment
     * @return
     */
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnrollment(@CurrentAccount Account account, @PathVariable String path,
                                   @PathVariable("eventId") Event event, @PathVariable("enrollmentId")Enrollment enrollment) {

        Study study = studyService.getStudyToUpdate(account, path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodedPath() + "/events/" + event.getId();
    }



}