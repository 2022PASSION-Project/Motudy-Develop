package com.motudy.infra;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc // @AutoConfigureWebTestClient를 하면 테스트 하면서 view 응답을 다 보여줌. 즉, view test가 가능
public @interface MockMvcTest {
}
