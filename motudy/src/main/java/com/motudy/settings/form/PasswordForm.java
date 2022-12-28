package com.motudy.settings.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {

    @Length(min = 8, max = 25)
    private String newPassword;

    @Length(min = 8, max = 25)
    private String newPasswordConfirm;
}
