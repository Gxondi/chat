package com.hyh.mallchat.common.common.event;

import com.hyh.mallchat.common.user.domain.entity.User;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserOnlineEvent extends ApplicationEvent {
    private User user;
    public UserOnlineEvent(Object source,User user) {
        super(source);
        this.user = user;
    }
}
