package com.motudy.modules.account.validator;

import com.motudy.modules.account.form.PasswordForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// 이 Validator는 다른 빈을 사용한 게 없음
public class PasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        // PasswordForm 타입의 할당 가능한 타입이면
        return PasswordForm.class.isAssignableFrom(PasswordForm.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordForm passwordForm = (PasswordForm) target;
        if(!passwordForm.getNewPassword().equals(passwordForm.getNewPasswordConfirm())) {
            errors.rejectValue("newPassword", "wrong.value", "입력하신 패스워드와 확인 패스워드가 일치하지 않습니다.");
        }
    }
}
