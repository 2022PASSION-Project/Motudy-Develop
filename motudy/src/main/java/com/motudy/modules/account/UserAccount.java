package com.motudy.modules.account;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/**
 * 인증된 사용자
 */
@Getter
public class UserAccount extends User {

    /**
     * @CurrentUser에서 account라는 프로퍼티를 꺼내라고 하면 이걸 꺼냄
     */
    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.account = account;
    }
}
