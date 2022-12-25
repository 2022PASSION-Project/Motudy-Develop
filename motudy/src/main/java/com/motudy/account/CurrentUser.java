package com.motudy.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 런타임까지도 유지
@Target(ElementType.PARAMETER) // 파라미터에만 붙일 수 있도록 함
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {
    /**
     * 현재 인증된 유저가 anonymous라면 account에는 null이 들어감
     */
}
