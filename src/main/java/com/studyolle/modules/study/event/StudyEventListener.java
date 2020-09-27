package com.studyolle.modules.study.event;

import com.studyolle.modules.study.Study;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class StudyEventListener {

    @EventListener
    public void handelStudyCreatedEvent(StudyCreatedEvent studyCreateEvent) {
        Study study = studyCreateEvent.getStudy();
        log.info(study.getTitle() + "is created.");
        //TODO 이메일을 보내거나 DB에 Notification을 이용하면됨.

        throw new RuntimeException();
    }
}