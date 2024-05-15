package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomHotFlagEnum {
    YES(1,"热点群"),
    NO(0,"非热点"),;
    private Integer type;
    private String desc;
}
