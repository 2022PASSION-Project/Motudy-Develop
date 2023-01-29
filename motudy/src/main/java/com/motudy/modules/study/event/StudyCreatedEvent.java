package com.motudy.modules.study.event;

import com.motudy.modules.study.Study;
import lombok.Data;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
