package com.motudy.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // readOnly = true 옵션을 넣으면 성능상 이점을 가질 수 있음. 단 read만 됨.
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    Account findByEmail(String email);
    Account findByNickname(String nickname);
}