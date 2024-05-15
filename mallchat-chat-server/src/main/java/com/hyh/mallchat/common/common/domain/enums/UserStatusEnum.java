package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
@AllArgsConstructor
@Getter
public enum UserStatusEnum {
    ONLINE(1,"在线"),
    OFFLINE(2,"离线");
    private Integer type;
    private String desc;


}
