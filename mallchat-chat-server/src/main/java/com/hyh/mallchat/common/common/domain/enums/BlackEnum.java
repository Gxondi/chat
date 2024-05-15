package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BlackEnum {
    UID(1,"UID"),
    IP(2,"IP");
    private Integer type;
    private String desc;
}
