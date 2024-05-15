package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Getter
public class RegisterEvent extends ApplicationEvent {
    private User user;
    public RegisterEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
