package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyMsgStatusEnum {
    UNREAD(1,"未读"),
    READ(2,"已读"),;
    private Integer type;
    private String desc;
}
