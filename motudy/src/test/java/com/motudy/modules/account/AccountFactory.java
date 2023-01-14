package com.motudy.modules.account;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFactory {

    @Autowired AccountRepository accountRepository;

    public Account createAccount(String nickname) {
        Account student = new Account();
        student.setNickname(nickname);
        student.setEmail(nickname + "@email.com");
        accountRepository.save(student);
        return student;
    }
}
/**
 * 테스트에 필요한 객체를 만들 때
 * 각각의 패키지 안에다가 Factory 클래스를 만들어서 활용
 */