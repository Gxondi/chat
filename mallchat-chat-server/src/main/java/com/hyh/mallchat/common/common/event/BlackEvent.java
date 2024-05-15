package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class BlackEvent extends ApplicationEvent {
    private User user;
    public BlackEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
