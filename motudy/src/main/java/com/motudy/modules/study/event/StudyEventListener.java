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
import java.util.HashSet;
import java.util.Set;

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
                sendStudyCreatedEmail(study, account, "새로운 스터디가 생겼습니다.",
                        "[모터디 알림] : '" + study.getTitle() + "' 스터디가 생겼습니다.");
            }
            if(account.isStudyCreatedByWeb()) { // account가 study created를 웹 알림으로 받겠다고 했으면
                createNotification(study, account, "새로운 스터디가 생겼습니다.", NotificationType.STUDY_CREATED);
            }
        });
    }

    @EventListener
    public void handleStudyUpdateEvent(StudyUpdateEvent studyUpdateEvent) {
        Study study = studyRepository.findStudyWithManagersAndMembersById(studyUpdateEvent.getStudy().getId());
        Set<Account> accounts = new HashSet<>();
        accounts.addAll(study.getManagers());
        accounts.addAll(study.getMembers());

        accounts.forEach(account -> {
            if(account.isStudyUpdatedByEmail()) {
                sendStudyCreatedEmail(study, account, studyUpdateEvent.getMessage(),
                        "[모터디 알림] : '" + study.getTitle() + "' 스터디에 새 소식이 있습니다.");
            }
            if(account.isStudyUpdatedByWeb()) {
                createNotification(study, account, studyUpdateEvent.getMessage(), NotificationType.STUDY_UPDATED);
            }
        });
    }

    private void createNotification(Study study, Account account, String message, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setTitle(study.getTitle());
        notification.setLink("/study/" + study.getEncodedPath());
        notification.setChecked(false);
        notification.setCreatedDateTime(LocalDateTime.now());
        notification.setMessage(message);
        notification.setAccount(account);
        notification.setNotificationType(notificationType);
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedEmail(Study study, Account account, String contextMessage, String emailSubject) {
        Context context = new Context();
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("nickname", account.getNickname());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", contextMessage);
        context.setVariable("host", appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .subject(emailSubject)
                .to(account.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
