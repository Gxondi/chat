package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.user.domain.dto.MsgRecallDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class RecallEvent extends ApplicationEvent {
    private final MsgRecallDTO recallDTO;
    public RecallEvent(Object source, MsgRecallDTO recallDTO) {
        super(source);
        this.recallDTO = recallDTO;
    }
}
