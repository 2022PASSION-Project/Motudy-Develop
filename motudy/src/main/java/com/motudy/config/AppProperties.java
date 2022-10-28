package com.motudy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app") // prefix를 app에 해당하는 바인딩을 받겠다
public class AppProperties {

    private String host;
}
