package com.motudy;

import com.motudy.account.AccountService;
import com.motudy.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

/**
 * 빈을 주입받을 수 있다.
 */
@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        String nickname = withAccount.value();

        /**
         * 유저를 만들고나서 시큐리티에 넣음
         * @BeforeEach이 했던 유저 생성을 그대로 가져옴
         */
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname(nickname);
        signUpForm.setPassword(nickname + "@email.com");
        signUpForm.setPassword("12345678");
        accountService.processNewAccount(signUpForm);

        /**
         * WithAccount라는 이 에노테이션에서 필요한 정보만 받으면 되지만 현재 닉네임만 필요
         * nickname에 대한 유저 정보를 로드해서 시큐리티 컨텍스트에다가 넣는다.
         * @WithUserDetails 이 하는 일이랑 같다.
         */
        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}
