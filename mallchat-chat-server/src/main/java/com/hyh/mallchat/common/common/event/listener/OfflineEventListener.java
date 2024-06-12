package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.common.event.BlackEvent;
import com.hyh.mallchat.common.common.event.UserOfflineEvent;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OfflineEventListener {

    @Autowired
    private WebSocketService webSocketService;

    @Async
    @EventListener(classes = UserOfflineEvent.class)
    public void saveRedisAndPush(UserOfflineEvent userOfflineEvent) {
        User user = userOfflineEvent.getUser();
        //发送消息
        webSocketService.sendToAllOnline(WebSocketAdapter.buildOfflineNotifyResp(userOfflineEvent.getUser()), userOfflineEvent.getUser().getId());
    }

}
