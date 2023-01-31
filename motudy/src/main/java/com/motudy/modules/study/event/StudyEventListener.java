package com.motudy.modules.study.event;

import com.motudy.infra.config.AppProperties;
import com.motudy.infra.mail.EmailMessage;
import com.motudy.infra.mail.EmailService;
import com.motudy.modules.account.Account;
import com.motudy.modules.account.AccountPredicates;
import com.motudy.modules.account.AccountRepository;
import com.motudy.modules.notification.Notification;
import com.motudy.modules.notification.NotificationRepository;
import com.motudy.modules.notification.NotificationType;
import com.motudy.modules.study.Study;
import com.motudy.modules.study.StudyRepository;
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
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId());
        // study의 tag과 zons에 매핑되는 account를 찾는 것
        Iterable<Account> accounts = accountRepository.findAll(AccountPredicates.findByTagsAndZones(study.getTags(), study.getZones()));
        accounts.forEach(account -> { // 순회하면서
            if(account.isStudyCreatedByEmail()) { // account가 study created를 이메일로 받겠다고 했으면
                sendStudyCreatedEmail(study, account);
            }
            if(account.isStudyCreatedByWeb()) { // account가 study created를 웹 알림으로 받겠다고 했으면
                saveStudyCreatedNotification(study, account);
            }
        });
    }

    private void saveStudyCreatedNotification(Study study, Account account) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(study.getShortDescription());
        notification.setAccount(account);
        notification.setNotificationType(NotificationType.STUDY_CREATED);
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Study study, Account account) {
        Context context = new Context();
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생겼습니다.");
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject("[모터디 알림] : '" + study.getTitle() + "'스터디가 생겼습니다")
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
