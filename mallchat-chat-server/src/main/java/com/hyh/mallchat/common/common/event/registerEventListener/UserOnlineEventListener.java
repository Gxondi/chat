package com.hyh.mallchat.common.common.event.registerEventListener;

import com.hyh.mallchat.common.common.domain.enums.UserStatusEnum;
import com.hyh.mallchat.common.common.event.UserOnlineEvent;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import com.hyh.mallchat.common.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserOnlineEventListener {

    @Autowired
    private UserDao userDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;
    @Autowired
    private IpService ipService;

    /**
     * 用户上线事件
     * ip解析可以失败，但是不影响用户上线
     * @param userOnlineEvent
     */
    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT,fallbackExecution = true)
    public void saveDB(UserOnlineEvent userOnlineEvent) {
        User user = userOnlineEvent.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setActiveStatus(UserStatusEnum.ONLINE.getType());
        update.setIpInfo(user.getIpInfo());
        userDao.updateById(update);
        //ip详情解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
