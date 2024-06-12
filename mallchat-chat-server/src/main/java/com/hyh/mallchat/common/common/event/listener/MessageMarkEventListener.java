package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.dao.MessageMarkDao;
import com.hyh.mallchat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.hyh.mallchat.common.chat.domain.dto.MsgSendMessageDTO;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.MessageMark;
import com.hyh.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.common.constant.MQConstant;
import com.hyh.mallchat.common.common.domain.enums.IdempotentEnum;
import com.hyh.mallchat.common.common.domain.enums.ItemEnum;
import com.hyh.mallchat.common.common.event.MessageMarkEvent;
import com.hyh.mallchat.common.common.event.MessageSendEvent;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import com.hyh.mallchat.common.user.service.impl.PushService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
public class MessageMarkEventListener {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private PushService pushService;
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private IUserBackpackService iUserBackpackService;

    /**
     * 监听到点赞消息后进行一系列业务操作
     *
     * @param messageMarkEvent
     */
    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void changeMsgType(MessageMarkEvent messageMarkEvent) {
        ChatMessageMarkDTO dto = messageMarkEvent.getDto();
        Message message = messageDao.getById(dto.getMsgId());
        if (!Objects.equals(message.getType(), MessageTypeEnum.TEXT)) {
            return;
        }
        Integer count = messageMarkDao.getCount(dto.getMsgId(), dto.getMarkType());
        MessageMarkTypeEnum markTypeEnum = MessageMarkTypeEnum.of(dto.getMarkType());
        if (count < markTypeEnum.getRiseNum()) {
            return;
        }
        //发放徽章
        if (MessageMarkTypeEnum.DISLIKE.getType().equals(dto.getMarkType())) {
            iUserBackpackService.acquireItem(dto.getUid(), ItemEnum.LIKE_BADGE.getId(), IdempotentEnum.MSG_ID, dto.getMsgId().toString());
        }
    }

    @Async
    @TransactionalEventListener(classes = MessageMarkEvent.class, fallbackExecution = true)
    public void notify(MessageMarkEvent messageMarkEvent) {
        ChatMessageMarkDTO dto = messageMarkEvent.getDto();
        Long msgId = dto.getMsgId();
        Integer markType = dto.getMarkType();
        Integer count = messageMarkDao.getCount(msgId, markType);
        pushService.sendAllOnlineUser(new WebSocketAdapter().buildMsgMark(dto, count));
    }
}
