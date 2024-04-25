package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IdempotentEnum {
    UID(1,"UID"),
    MSG_ID(2,"消息id");
    private Integer type;
    private String desc;
}
