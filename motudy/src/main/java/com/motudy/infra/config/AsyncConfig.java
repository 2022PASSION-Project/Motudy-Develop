package com.motudy.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        log.info("processors count : {}", processors);
        executor.setCorePoolSize(processors); // CPU // 원래 주던 튜브 총 개수
        executor.setMaxPoolSize(processors * 2); // 또는 하는 작업에 따라 달라짐 // 재고까지 합해서 튜브 총 개수
        executor.setQueueCapacity(50); // 메모리에 따라 달라짐 // 10개 튜브 다 줬으므로, 추가로 50명의 사람들이 줄 서고 있음(11번째부터)
        executor.setKeepAliveSeconds(60); // 수영장 튜브 뒀다가 언제 싹다 회수할 거냐
        executor.setThreadNamePrefix("[AsyncExecutor] : ");
        executor.initialize(); // 이거 안 하면 ThreadPoolTaskExecutor 에러 발생. 초기화 해야 함
        return executor;
    }
}

/** ThreadPoolTaskExecutor
 * CorePoolSize, MaxPoolSize, QueueCapacity
 * 처리할 태스크(이벤트)가 생겼을 때,
 * '현재 일하고 있는 쓰레드 개수'(active thread)가 '코어 개수'(core pool size)보다 작으면, 남아있는 쓰레드를 사용한다.
 * '현재 일하고 있는 쓰레드 개수'가 코어 개수만큼 차있으면 '큐 용량(queue capacity)'이 찰 때까지 큐에 쌓아둔다.
 * 큐 용량이 다 차면, 코어 개수를 넘어서 '맥스 개수'(max pool size)에 다르기 전까지 새로운 쓰레드를 만들어 처리한다.
 * 맥스 개수를 넘기면 태스크를 처리하지 못한다.
 */