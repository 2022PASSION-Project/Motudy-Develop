package com.motudy.modules.study;

import com.motudy.modules.account.QAccount;
import com.motudy.modules.tag.QTag;
import com.motudy.modules.zone.QZone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class StudyRepositoryExtensionImpl extends QuerydslRepositorySupport implements StudyRepositoryExtension {

    /**
     * 상속 받은 클래스는 상위 클래스의 기본 생성자를 호출하려고 함
     * 그런데 지금은 상위 클래스(추상 클래스)에는 기본 생성자가 없음
     * 그래서 그 기본 생성자가 아닌 생성자를 구현해야 함
     *
     * 부모 생성자에 넘겨준 파라미터를 가지고 있는 자식 클래스의 생성자를 만들어준다.
     * 하지만, 우리는 어떤 도메인을 다루는 Repository인지 알고 있다.
     * 그렇기 때문에, 다른 곳에서 받아올 필요가 없고, 넘겨주기만 하면 된다.
     */
    public StudyRepositoryExtensionImpl() {
        super(Study.class);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        QStudy study = QStudy.study;
        JPQLQuery<Study> query = from(study).where(study.published.isTrue() // 공개된 스터디 중
                        .and(study.title.containsIgnoreCase(keyword)) // 대소문자 관계없이 keyword라는 제목을 가진 스터디이고,
                        .or(study.tags.any().title.containsIgnoreCase(keyword)) // 대소문자 관계없이 keyword를 포함하고 있는 태그를 가지고 있거나,
                        .or(study.zones.any().localNameOfCity.containsIgnoreCase(keyword))) // 또는 로컬 이름에 keyword가 들어가있으면
                .leftJoin(study.tags, QTag.tag).fetchJoin()
                .leftJoin(study.zones, QZone.zone).fetchJoin()
                .leftJoin(study.members, QAccount.account).fetchJoin()
                .distinct(); // study에 대한 distinct가 아니라 전체 쿼리에 대한 distinct, 그래서 사실 의미없긴 함
        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Study> fetchResults = pageableQuery.fetchResults();// fetchResults()는 페이징 처리할 때 사용
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}