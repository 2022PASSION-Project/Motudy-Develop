package com.motudy.infra.mail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailMessage {

    private String to; // 누구에게 보낼 건지

    private String subject; // 제목

    private String message; // 메시지
}
