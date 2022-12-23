package com.motudy.account;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // @AutoConfigureWebTestClient를 하면 테스트 하면서 view 응답을 다 보여줌. 즉, view test가 가능
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    /**
     * 시큐리티가 적용되면
     * sign-up 폼에 접근할 때 access denied exception이 발생하고
     * 이걸 시큐리티 필터가 잡아서 로그인 폼 생성과 함께 폼을 보여주도록 함
     * view가 제공된다고 볼 수 있음
     */
    @DisplayName("회원 가입 화면 보이는지 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(view().name("account/sign-up")) // 뷰
                .andExpect(model().attributeExists("signUpForm")); // 모델 객체
    }
}