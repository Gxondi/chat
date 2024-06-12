package com.hyh.mallchat.common.chat.domain.enums;

import lombok.Getter;

@Getter
public enum MessageTypeEnum {
    TEXT(1, "文本消息"),
    IMAGE(2, "图片消息"),
    VOICE(3, "语音消息"),
    VIDEO(4, "视频消息"),
    FILE(5, "文件消息"),
    SYSTEM(6, "系统消息"),
    RECALL(7, "撤回消息"),
    FORWARD(8, "转发消息"),
    REPLY(9, "回复消息"),
    ;

    private Integer type;
    private String desc;

    MessageTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

}
