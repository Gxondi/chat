package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomNormalOrNoEnum {
    NORMAL(0,"正常"),
    NO(1,"禁用"),;
    private Integer type;
    private String desc;
}
