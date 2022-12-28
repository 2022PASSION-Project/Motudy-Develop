package com.motudy.account.validator;

import com.motudy.account.AccountRepository;
import com.motudy.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// 빈을 주입받아야 하므로, 스프링은 빈들만 자동으로 의존성을 주입받을 수 있음.
// 자동으로 되지 않는 것들은 필요하면 명시적으로 넣어줘야 함
@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target; // target : 실제 들어오는 객체
        if(accountRepository.existsByEmail(signUpForm.getEmail())) {
            // 이미 있는 이메일이면 reject
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용 중인 이메일입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())) {
            // 이미 있는 닉네임이면 reject
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용 중인 닉네임입니다.");
        }
    }
}
