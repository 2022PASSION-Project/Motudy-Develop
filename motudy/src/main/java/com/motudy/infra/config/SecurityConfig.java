package com.motudy.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    // JPA를 사용하고 있기 때문에 당연히 Bean으로 등록이 되어 있음. 가져다 쓰기만 하면 됨
    private final DataSource dataSource;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email-token",
                        "/email-login", "/check-email-login", "/login-link").permitAll()
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login").permitAll(); // 커스텀한 페이지의 URL 넣을 수 있음

        http.logout()
                .logoutSuccessUrl("/"); // 로그아웃이 성공했을 때 어디로 보낼지 URL

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository());
//                .key("12345678")// 해싱 기반의 쿠키를 만드는 방법 -> 결코 안전하지 않음

        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        /**
         * public static final String CREATE_TABLE_SQL = "create table persistent_logins (username varchar(64) not null, series varchar(64) primary key, "
         * 			+ "token varchar(64) not null, last_used timestamp not null)";
         * 		-> 이 Repository가 사용하는 Table이 있음. 	이 schema에 해당하는 table이 있어야 함.
         * 	근데 JPA를 쓰고 있고, InmemoryDB를 쓸 때는 Entity 정보를 이용하여 자동으로 테이블이 생성됨.
         * 	위 스키마가 생성될 수 있도록 매핑이 되는 엔티티를 추가해야 함
         */
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
