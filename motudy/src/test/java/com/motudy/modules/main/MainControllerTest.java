package com.motudy.modules.main;

import com.motudy.modules.account.AccountRepository;
import com.motudy.modules.account.AccountService;
import com.motudy.modules.account.form.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // ExtendWith가 이미 들어있음. 메타 어노테이션 사용됨
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("motudy");
        signUpForm.setEmail("motudy@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @DisplayName("이메일로 로그인 성공")
    @Test
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "motudy@email.com")
                .param("password", "12345678")
                .with(csrf())) // 폼에 계정 정보 전달할 때 csrf토큰도 같이 보냄
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("motudy"));
        /* 왜 motudy라는 이름을 가지고 로그인이 될까?
            UserAccount에서 username 부분을 nickname으로 리턴했기 때문
            -> 그래서 motudy라는 닉네임으로 로그인이 됨
         */
    }

    @DisplayName("닉네임으로 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "motudy")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("motudy"));
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "12341234")
                .param("password", "12345678")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithMockUser
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
    /**
     * @WithMockUser
     * 기본적으로 테스트를 실행할 때
     * Authentication의 스프링 시큐리티의 User,(시큐리티에 User 클래스가 있음 ㅇㅇ)
     * User 클래스 타입에 해당하는 principal로 username이 유저, password가 비밀번호인 걸 하나 넣어줌
     * 진짜 인증된 유저가 있는 경우에, 로직이 있는 테스트에 유용하게 쓰일 수 있음
     */
}