package com.motudy.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Study {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers;

    @ManyToMany
    private Set<Account> members;

    @Column(unique = true)
    private String path;

    private String title; // 스터디 제목은 중복이 되어도 괜찮을 것 같음

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image; // 배너 이미지

    @ManyToMany
    private Set<Tag> tags; // 관심 주제

    @ManyToMany
    private Set<Zone> zones; // 지역 정보

    private LocalDateTime publishedDateTime; // 스터디를 공개한 시간

    private LocalDateTime closedDateTime; // 스터디를 종료한 시간

    private LocalDateTime recruitingUpdatedDateTime; // 인원 모집 시간

    private boolean recruiting; // 인원 모집 중인지

    private boolean published; // 스터디 공개 여부

    private boolean closed; // 스터디 종료 여부

    private boolean useBanner; // 배너 사용 여부
}
