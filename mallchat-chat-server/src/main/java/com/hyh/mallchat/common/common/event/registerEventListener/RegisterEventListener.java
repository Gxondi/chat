package com.hyh.mallchat.common.common.event.registerEventListener;

import com.hyh.mallchat.common.common.domain.enums.IdempotentEnum;
import com.hyh.mallchat.common.common.domain.enums.ItemEnum;
import com.hyh.mallchat.common.common.event.RegisterEvent;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RegisterEventListener {

    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;

    @Async
    @TransactionalEventListener(classes = RegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendCard(RegisterEvent registerEvent) {
        User user = registerEvent.getUser();
        //发送卡片
        iUserBackpackService.acquireItem(user.getId(), ItemEnum.MODIFY_NAME_CARD.getId(), IdempotentEnum.UID, user.getId().toString());
    }

    @Async
    @TransactionalEventListener(classes = RegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void sendBadge(RegisterEvent registerEvent) {
        User user = registerEvent.getUser();
        int count = userDao.count();
        if (count < 10) {
            //发送卡片
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP10_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        } else if (count < 100) {
            iUserBackpackService.acquireItem(user.getId(), ItemEnum.REG_TOP100_BADGE.getId(), IdempotentEnum.UID, user.getId().toString());
        }
    }
}
