package com.motudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MotudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotudyApplication.class, args);
    }

}

/**
 * 모터디의 선택
 * 1. 데이터 변경은 Service 계층으로 위임해서 트랜잭션 안에서 처리한다.
 * 2. 데이터 조회는 Repository 또는 Service를 사용한다.
 */