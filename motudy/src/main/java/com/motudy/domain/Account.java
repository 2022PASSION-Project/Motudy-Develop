package com.motudy.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/*
    @EqualsAndHashCode(of = "id")
	엔티티 클래스에서
	EqualsAndHashCode로 id만 뺀 이유?

	연관관계가 복잡해질 때
    EqualsAndHashCode에서 서로 다른 연관관계를 계속해서 순환 참조하느라
    무한루프가 발생하고, 스택오버플로우가 발생할 수 있음
*/

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio; // 자기소개

    private String url;

    private String occupation;

    private String location; // 희망 지역, default varchar(255)

    @Lob @Basic(fetch = FetchType.EAGER) // 유저를 로딩할 때 어차피 이미지 가지고 와야되므로
    private String profileImage;

    // 스터디 만들어졌을 때 알림
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;

    // 스터디 가입 신청 결과 알림
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;

    // 스터디 수정 시 알림
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;
}
