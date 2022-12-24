package com.motudy.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class SignUpForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9_-]{3,20}$")
    private String nickname;

    @Email // 이메일 형식의 문자여야한다.
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 25)
    private String password;
}
