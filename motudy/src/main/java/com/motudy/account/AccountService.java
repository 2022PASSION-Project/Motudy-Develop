package com.motudy.account;

import com.motudy.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    @Transactional // persist 상태의 객체는 트랜잭션이 끝날 때 상태를 DB에 sink 한다.
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {
        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByWeb(true)
                .studyUpdatedByWeb(true)
                .build();
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("모터디, 회원 가입 인증"); // 메일 제목
        mailMessage.setText("/check-email-token?token=" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail()); // 메일 본문
        javaMailSender.send(mailMessage);
    }

    /**
     * 정석적인 방법은 AuthenticationManager --> authenticationManager.authenticate(token) 으로 인증을 거친 방법임.
     * 이때 plane text로 받은 비밀번호를 써야 하는데, 이거는 DB에 넣을 필요도 없고 쓰이기 힘듦.
     * 하지만 지금은 정석적인 방법으로 하지 않음.
     * 먼저 getPassword()를 통해서 password를 받으면 encoding된 password를 받게 됨.
     * 그래서 UsernamePasswordAuthenticationToken의 생성자로
     * 마지막 파라미터에 SimpleGrantedAuthority("ROLE_USER")로 권한 목록을 받음(바로 다 주입한다).
     * SecurityContextHolder에 setAuthentication(token)을 통하여 token에 대한 authentication을 세팅함.
     */
    public void login(Account account) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account), // principal 객체
                account.getPassword(), // password
                List.of(new SimpleGrantedAuthority("ROLE_USER"))); // 권한(authority)
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        // 이메일로 찾기
        Account account = accountRepository.findByEmail(emailOrNickname); 
        if(account == null) {
            // 닉네임으로 찾기
            account = accountRepository.findByNickname(emailOrNickname);
        }

        // 유저가 없다?
        if(account == null) { // 그래도 못 찾았으면
            // 이메일 또는 패스워드가 잘못됐다
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(account);
    }
    /*
     * 정석적인 방법
     * private final AuthenticationManager authenticationManager; // 빈 생성
     *
     * UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
     *      account.getNickname(), account.getPassword());
     * Authentication authentication = authenticationManager.authenticate(token);
     * SecurityContext context = SecurityContextHolder.getContext();
     * context.setAuthentication(authentication);
     */
}
