package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.chat.domain.entity.Message;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageSendEvent extends ApplicationEvent {
    private Long msgId;
    public MessageSendEvent(Object source, Long msgId) {
        super(source);
        this.msgId=(msgId);
    }

    public Long getMsgId() {
        return msgId;
    }
}
