package com.motudy.domain;

import com.motudy.account.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudyTest {

    Study study;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        study = new Study();
        account = new Account();
        account.setNickname("motudy");
        account.setPassword("123");
        userAccount = new UserAccount(account);
    }

    @DisplayName("스터디를 공개하면서 인원을 모집하고 있고, 이미 멤버나 스터디 관리자가 아니라면 스터디 가입 가능")
    @Test
    void isJoinable() {
        study.setPublished(true);
        study.setRecruiting(true);

        assertTrue(study.isJoinable(userAccount));
    }

    @DisplayName("스터디를 공개하면서 인원 모집 중이더라도, 스터디 관리자는 스터디 가입이 불필요하다.")
    @Test
    void isJoinable_false_for_manager() {
        study.setPublished(true);
        study.setRecruiting(true);
        study.addManager(account);

        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디를 공개하면서 인원 모집 중이더라도, 스터디 멤버는 스터디 재가입이 불필요하다.")
    @Test
    void isJoinable_false_for_member() {
        study.setPublished(true);
        study.setRecruiting(true);
        study.addMember(account);

        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디가 비공개거나 인원 모집 중이 아니면 스터디 가입이 불가능하다.")
    @Test
    void isJoinable_false_for_non_recruiting_study() {
        study.setPublished(true);
        study.setRecruiting(false);

        assertFalse(study.isJoinable(userAccount));

        study.setPublished(false);
        study.setRecruiting(true);

        assertFalse(study.isJoinable(userAccount));
    }

    @DisplayName("스터디 관리자인지 확인")
    @Test
    void isManager() {
        study.addManager(account);
        assertTrue(study.isManager(userAccount));
    }

    @DisplayName("스터디 멤버인지 확인")
    @Test
    void isMember() {
        study.addMember(account);
        assertTrue(study.isMember(userAccount));
    }
}
