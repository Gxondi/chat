package com.hyh.mallchat.common.common.domain.enums;

import lombok.Getter;

@Getter
public enum MessageModel {
    BROADCASTING("BROADCASTING"),
    CLUSTERING("CLUSTERING");

    private final String messageModel;
    MessageModel(String messageModel) {
        this.messageModel = messageModel;
    }

    public String getMessageModel() {
        return this.messageModel;
    }
}
