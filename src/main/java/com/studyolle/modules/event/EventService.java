package com.studyolle.modules.event;

import com.studyolle.modules.account.Account;
import com.studyolle.modules.event.event.EnrollmentAcceptedEvent;
import com.studyolle.modules.event.event.EnrollmentRejectedEvent;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.event.form.EventForm;
import com.studyolle.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollMentRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 모임 생성
     * @param event
     * @param study
     * @param account
     * @return
     */
    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 만들었습니다."));
        return eventRepository.save(event);
    }

    /**
     * 모임 수정
     * @param event
     * @param eventForm
     */
    public void updateEvent(Event event, EventForm eventForm) {
        modelMapper.map(eventForm, event);
        //TODO 모집 인원을 늘린 선착순 모임의 경우에, 자동으로 추가 인원의 참가 신청을 확정 상태로 변경해야 한다. (나중에 할 일)
        event.acceptWaitingList();
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임 정보를 수정했습니다."));
        event.acceptWaitingList();
    }

    public void deleteEvent(Event event) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(event.getStudy(),
                "'" + event.getTitle() + "' 모임을 취소했습니다."));
    }

    public void newEnrollment(Event event, Account account) {

        if (!enrollMentRepository.existsByEventAndAccount(event, account)) {
            Enrollment enrollment = new Enrollment();
            enrollment.setEnrolledAt(LocalDateTime.now());
            enrollment.setAccepted(event.isAbleToAcceptWaitingEnrollment());
            enrollment.setAccount(account);
            event.addEnrollment(enrollment);
            enrollMentRepository.save(enrollment);
        }
    }

    public void cancelEnrollment(Event event, Account account) {
        Enrollment enrollment = enrollMentRepository.findByEventAndAccount(event, account);
        if (!enrollment.isAccepted()) {
            event.removeEnrollment(enrollment);
            enrollMentRepository.delete(enrollment);
            event.acceptNextWaitingEnrollment();
        }
    }

    public void acceptEnrollment(Event event, Enrollment enrollment) {
        event.accept(enrollment);
        eventPublisher.publishEvent(new EnrollmentAcceptedEvent(enrollment));
    }

    public void rejectEnrollment(Event event, Enrollment enrollment) {
        event.reject(enrollment);
        eventPublisher.publishEvent(new EnrollmentRejectedEvent(enrollment));
    }

    public void checkInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(true);
    }

    public void cancelCheckInEnrollment(Enrollment enrollment) {
        enrollment.setAttended(false);
    }
}