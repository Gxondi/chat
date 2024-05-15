package com.hyh.mallchat.common.common.event.registerEventListener;

import com.hyh.mallchat.common.common.event.BlackEvent;
import com.hyh.mallchat.common.common.event.UserApplyEvent;
import com.hyh.mallchat.common.user.dao.UserApplyDao;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserApply;
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
public class UserApplyEventListener {

    @Autowired
    private UserApplyDao userApplyDao;
    private WebSocketService webSocketService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void notifyFriend(UserApplyEvent userApplyEvent) {
        UserApply userApply = userApplyEvent.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        //发送消息
        webSocketService.sendPushMsg(userApply.getTargetId(),WebSocketAdapter.buildUserApplyMsg(userApply.getUid(), unReadCount));
    }

}
