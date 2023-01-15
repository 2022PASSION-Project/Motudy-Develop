package com.motudy.modules.event;

import com.motudy.infra.AbstractContainerBaseTest;
import com.motudy.infra.MockMvcTest;
import com.motudy.modules.account.Account;
import com.motudy.modules.account.AccountFactory;
import com.motudy.modules.account.AccountRepository;
import com.motudy.modules.account.WithAccount;
import com.motudy.modules.study.Study;
import com.motudy.modules.study.StudyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class EventControllerTest extends AbstractContainerBaseTest {

    @Autowired MockMvc mockMvc;
    @Autowired StudyFactory studyFactory;
    @Autowired AccountFactory accountFactory;
    @Autowired EventService eventService;
    @Autowired AccountRepository accountRepository;
    @Autowired EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("motudy")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account anonymous = accountFactory.createAccount("anonymous");
        Study study = studyFactory.createStudy("test-study", anonymous);
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
        Account anonymous = accountFactory.createAccount("anonymous");
        Study study = studyFactory.createStudy("test-study", anonymous);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, anonymous);

        Account student = accountFactory.createAccount("student");
        Account teacher = accountFactory.createAccount("teacher");
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
        Account anonymous = accountFactory.createAccount("anonymous");
        Account student = accountFactory.createAccount("student");
        Study study = studyFactory.createStudy("test-study", anonymous);
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

    @Test
    @DisplayName("참가 신청이 확정되지 않은 사람이 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("motudy")
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account motudy = accountRepository.findByNickname("motudy");
        Account anonymous = accountFactory.createAccount("anonymous");
        Account student = accountFactory.createAccount("student");
        Study study = studyFactory.createStudy("test-study", anonymous);
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
        Account anonymous = accountFactory.createAccount("anonymous");
        Study study = studyFactory.createStudy("test-study", anonymous);
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

    private void isNotExist(Event event, Account account) {
        assertNull(enrollmentRepository.findByEventAndAccount(event, account));
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
}
