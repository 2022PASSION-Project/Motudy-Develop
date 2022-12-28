package com.motudy.settings;

import com.motudy.WithAccount;
import com.motudy.account.AccountRepository;
import com.motudy.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.motudy.settings.SettingsController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountRepository accountRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        // 커스터마이징으로 만든 @WithAccount("motudy") 계정을 매 테스트마다 지워야 함
        accountRepository.deleteAll();
    }

    @WithAccount("motudy")
    @DisplayName("닉네임 수정 폼")
    @Test
    void updateAccountForm() throws Exception {
        mockMvc.perform(get(SETTINGS_ACCOUNT_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("motudy")
    @DisplayName("닉네임 수정하기 - 입력값 정상")
    @Test
    void updateAccount() throws Exception {
        String newNickname = "studyGroup";
        mockMvc.perform(post(SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_ACCOUNT_URL))
                .andExpect(flash().attributeExists("message"));

        assertNotNull(accountRepository.findByNickname(newNickname));
    }

    @WithAccount("motudy")
    @DisplayName("닉네임 수정하기 - 입력값 에러")
    @Test
    void updateAccount_failure() throws Exception {
        String newNickname = "學習-工夫-開發"; // 학습-공부-개발
        mockMvc.perform(post(SETTINGS_ACCOUNT_URL)
                .param("nickname", newNickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_ACCOUNT_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"));
    }

    @WithAccount("motudy")
    @DisplayName("프로필 수정 폼")
    @Test
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));
    }

    @WithAccount("motudy")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"));

        Account motudy = accountRepository.findByNickname("motudy");
        assertEquals(bio, motudy.getBio());
    }

    @WithAccount("motudy")
    @DisplayName("프로필 수정하기 - 입력값 에러")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "소개를 더 길게 수정하는 경우. 소개를 좀 더 길게 수정하는 경우. 소개를 좀 많이 더 길게 수정하는 경우. 소개를 너무나도 길게 수정하는 경우. ";
        mockMvc.perform(post(SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"));

        Account motudy = accountRepository.findByNickname("motudy");
        assertNull(motudy.getBio());
    }

    @WithAccount("motudy")
    @DisplayName("패스워드 변경폼")
    @Test
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SETTINGS_PASSWORD_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }

    @WithAccount("motudy")
    @DisplayName("패스워드 변경 - 입력값 정상")
    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(post(SETTINGS_PASSWORD_URL)
                .param("newPassword", "asdfasdf")
                .param("newPasswordConfirm", "asdfasdf")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"));

        Account motudy = accountRepository.findByNickname("motudy");
        assertTrue(passwordEncoder.matches("asdfasdf", motudy.getPassword()));
    }

    @WithAccount("motudy")
    @DisplayName("패스워드 변경 - 입력값 에러 - 패스워드 불일치")
    @Test
    void updatePassword_error() throws Exception {
        mockMvc.perform(post(SETTINGS_PASSWORD_URL)
                .param("newPassword", "asdfasdf")
                .param("newPasswordConfirm", "qwerqwer")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"));
    }
}