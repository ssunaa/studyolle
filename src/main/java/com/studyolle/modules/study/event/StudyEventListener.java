package com.studyolle.modules.study.event;

import com.studyolle.infra.config.AppProperties;
import com.studyolle.infra.mail.EmailMessage;
import com.studyolle.infra.mail.EmailService;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.account.AccountPredicates;
import com.studyolle.modules.account.AccountRepository;
import com.studyolle.modules.notification.Notification;
import com.studyolle.modules.notification.NotificationRepository;
import com.studyolle.modules.notification.NotificationType;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handelStudyCreatedEvent(StudyCreatedEvent studyCreateEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreateEvent.getStudy().getId());
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> {
            if (account.isStudyCreatedByEmail()) {
                Context context = new Context();
                context.setVariable("nickname", account.getNickname());
                context.setVariable("link", "/study/" + study.getEncodedPath());
                context.setVariable("linkName" , study.getTitle());
                context.setVariable("message" , "새로운 스터디가 생겼습니다.");
                context.setVariable("host", appProperties.getHost());
                String message = templateEngine.process("mail/simple-link", context);

                EmailMessage emailMessage = EmailMessage.builder()
                        .subject("스터디올래, '" + study.getTitle() + "' 스터디가 생겼습니다.")
                        .to(account.getEmail())
                        .message(message)
                        .build();

                emailService.sendEmail(emailMessage);
            }

            if (account.isStudyCreatedByWeb()) {

                //TODO DB저장 (리파지토리 save활용)

            }
        });
    }
}