package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;
@Getter
public class UserOfflineEvent extends ApplicationEvent {
    private final User user;
    public UserOfflineEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
