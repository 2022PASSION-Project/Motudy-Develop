package com.motudy.settings.form;

import com.motudy.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    // Seoul(서울)/None
    private String zoneName;

    // Seoul
    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("("));
    }

    // Zone
    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1);
    }

    // ( 서울 )
    private String getLocalNameOfCity() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() {
        return Zone.builder().city(this.getCityName())
                .localNameOfCity(this.getLocalNameOfCity())
                .province(this.getProvinceName()).build();
    }
}
