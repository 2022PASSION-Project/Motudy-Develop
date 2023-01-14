package com.motudy.event;

import com.motudy.WithAccount;
import com.motudy.account.AccountRepository;
import com.motudy.domain.Account;
import com.motudy.domain.Event;
import com.motudy.domain.EventType;
import com.motudy.domain.Study;
import com.motudy.study.StudyControllerTest;
import com.motudy.study.StudyRepository;
import com.motudy.study.StudyService;
import com.motudy.study.StudySettingsControllerTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class EventControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyService studyService;
    @Autowired StudyRepository studyRepository;
    @Autowired AccountRepository accountRepository;
    @Autowired EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("motudy")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account anonymous = createAccount("anonymous");
        Study study = createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, anonymous);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account motudy = accountRepository.findByNickname("motudy");
        isAccepted(event, motudy);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("motudy")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account anonymous = createAccount("anonymous");
        Study study = createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, anonymous);

        Account student = createAccount("student");
        Account teacher = createAccount("teacher");
        eventService.newEnrollment(event, student);
        eventService.newEnrollment(event, teacher);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account motudy = accountRepository.findByNickname("motudy");
        isNotAccepted(event, motudy);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청이 확정된 사람이 도로 취소하는 경우, 자동으로 바로 다음 대기자의 신청을 확인한다.")
    @WithAccount("motudy")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account motudy = accountRepository.findByNickname("motudy");
        Account anonymous = createAccount("anonymous");
        Account student = createAccount("student");
        Study study = createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, anonymous);

        eventService.newEnrollment(event, student);
        eventService.newEnrollment(event, motudy);
        eventService.newEnrollment(event, anonymous);

        isAccepted(event, student);
        isAccepted(event, motudy);
        isNotAccepted(event, anonymous);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(event, student);
        isAccepted(event, anonymous);
        isNotExist(event, motudy);
    }

    private void isNotExist(Event event, Account account) {
        assertNull(enrollmentRepository.findByEventAndAccount(event, account));
    }

    @Test
    @DisplayName("참가 신청이 확정되지 않은 사람이 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("motudy")
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account motudy = accountRepository.findByNickname("motudy");
        Account anonymous = createAccount("anonymous");
        Account student = createAccount("student");
        Study study = createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, anonymous);

        eventService.newEnrollment(event, student);
        eventService.newEnrollment(event, anonymous);
        eventService.newEnrollment(event, motudy);

        isAccepted(event, student);
        isAccepted(event, anonymous);
        isNotAccepted(event, motudy);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(event, student);
        isAccepted(event, anonymous);
        isNotExist(event, motudy);
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청하기 - 대기중")
    @WithAccount("motudy")
    void newEnrollment_to_CONFIRMATIVE_event_not_accepted() throws Exception {
        Account anonymous = createAccount("anonymous");
        Study study = createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, anonymous);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account motudy = accountRepository.findByNickname("motudy");
        isNotAccepted(event, motudy);
    }

    private void isNotAccepted(Event event, Account account) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private void isAccepted(Event event, Account account) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }

    protected Study createStudy(String path, Account manager) {
        Study study = new Study();
        study.setPath(path);
        studyService.createNewStudy(study, manager);
        return study;
    }

    protected Account createAccount(String nickname) {
        Account study_group = new Account();
        study_group.setNickname(nickname);
        study_group.setEmail(nickname + "@email.com");
        accountRepository.save(study_group);
        return study_group;
    }
}
