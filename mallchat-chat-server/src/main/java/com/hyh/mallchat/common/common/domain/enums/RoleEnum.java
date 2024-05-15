package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1L,"超级管理员"),
    CHAT_MANAGER(2L,"聊天管理员");
    private Long type;
    private String desc;
}
