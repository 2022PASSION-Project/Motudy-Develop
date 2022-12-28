package com.motudy.settings.validator;

import com.motudy.account.AccountRepository;
import com.motudy.domain.Account;
import com.motudy.settings.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return NicknameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) target;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());
        if(byNickname != null) {
            // 이미 있는 닉네임이면 reject
            errors.rejectValue("nickname", "wrong.value","이미 사용 중인 닉네임입니다.");
        }
    }
}
