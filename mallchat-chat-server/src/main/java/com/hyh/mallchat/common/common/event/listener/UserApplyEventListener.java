package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.common.event.UserApplyEvent;
import com.hyh.mallchat.common.user.dao.UserApplyDao;
import com.hyh.mallchat.common.user.domain.entity.UserApply;
import com.hyh.mallchat.common.user.service.impl.PushService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserApplyEventListener {

    @Autowired
    private UserApplyDao userApplyDao;
    private PushService pushService;

    @Async
    @TransactionalEventListener(classes = UserApplyEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void notifyFriend(UserApplyEvent userApplyEvent) {
        UserApply userApply = userApplyEvent.getUserApply();
        Integer unReadCount = userApplyDao.getUnReadCount(userApply.getTargetId());
        //发送消息
        pushService.sendPushMsg(userApply.getTargetId(),WebSocketAdapter.buildUserApplyMsg(userApply.getUid(), unReadCount));
    }

}
