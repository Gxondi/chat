package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.common.event.BlackEvent;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BlackEventListener {

    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserCache userCache;
    @Async
    @TransactionalEventListener(classes = BlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(BlackEvent blackEvent) {
        User user = blackEvent.getUser();
        //发送消息
        webSocketService.sendToAllOnline(WebSocketAdapter.buildBlack(user.getId()));
    }

    @Async
    @TransactionalEventListener(classes = BlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void changeStatus(BlackEvent blackEvent) {
        User user = blackEvent.getUser();
        userDao.updateStatus(user.getId());
    }
    /**
     * 清除缓存
     * @param blackEvent
     */
    @Async
    @TransactionalEventListener(classes = BlackEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void evictCache(BlackEvent blackEvent) {
        userCache.evictGetBlackMap();
    }
}
