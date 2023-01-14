package com.motudy.modules.account.form;

import lombok.Data;

/**
 * 생성자를 직접 호출해서 만든 객체들이기 때문에 Bean이 아님
 */
@Data
public class Notifications {

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;
}
