package com.motudy.modules.event;

import com.motudy.modules.account.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

// 직접적인 연관관계에 있는 event와 event가 가지고 있는 study까지 같이 가져오고 싶을 때 서브 쿼리를 등록할 수 있음
@NamedEntityGraph(
        name = "Enrollment.withEventAndStudy",
        attributeNodes = { // "어떤 서브쿼리를 쓰겠냐" "서브쿼리로 study를 가져오겠다" "event의 study를 가져오겠다"
                @NamedAttributeNode(value = "event", subgraph = "study")
        },
        subgraphs = @NamedSubgraph(name = "study", attributeNodes = @NamedAttributeNode("study")) // study에 해당하는 subgraph를 정의
) // event의 study까지 같이 fetch를 하겠다
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
/**
 * @ManyToOne 일 때 기본 fetch 모드가 EAGER 모드이고, @OneToMany, @ManyToMany일 때는 LAZY다.
 * 이러한 FETCH 모드는 EntityManager를 통해서 id로 데이터를 가져올 때 적용된다.
 * 그러나, JPQL로 쿼리가 만들어져서 조회하는 경우에는 FETCH모드가 적용되지 않는다.
 * 그래서 EntityGraph를 쓴 것이다.
 */