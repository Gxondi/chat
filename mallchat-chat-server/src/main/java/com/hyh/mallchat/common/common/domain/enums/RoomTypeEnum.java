package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomTypeEnum {
    FRIEND(1,"单聊"),
    GROUP(2,"群聊"),;
    private Integer type;
    private String desc;
}
