package com.hyh.mallchat.common.common.domain.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum YesOrNoEnum {
    YES(1, "是"),
    NO(0, "否");
    private Integer status;
    private String desc;


    public static Integer toStatus(boolean b) {
        return b ? YES.getStatus() : NO.getStatus();
    }
}
