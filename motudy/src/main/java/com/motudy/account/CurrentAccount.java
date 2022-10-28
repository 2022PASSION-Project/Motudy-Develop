package com.motudy.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentAccount {
    /**
     * 현재 인증된 사용자 정보가 anonymousUser면 null 반환, 아니면 account 라는 프로퍼티를 뽑아옴
     * 'anonymousUser'로 permitAll()로 설정된 페이지들에 들어가면 익명 사용자로 접근됨
     */
}
