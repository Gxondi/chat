package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.dto.MsgSendMessageDTO;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.service.cache.MsgCache;
import com.hyh.mallchat.common.common.constant.MQConstant;
import com.hyh.mallchat.common.common.event.MessageSendEvent;
import com.hyh.mallchat.common.common.event.RecallEvent;
import com.hyh.mallchat.common.user.domain.dto.MsgRecallDTO;
import com.hyh.mallchat.common.user.service.impl.PushService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RecallEventListener {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private MsgCache msgCache;
    @Autowired
    private PushService pushService;
    @Async
    @TransactionalEventListener(classes = RecallEvent.class,fallbackExecution = true)
    public void evictMsg(RecallEvent recallEvent) {
        MsgRecallDTO recallDTO = recallEvent.getRecallDTO();
        msgCache.evictMsg(recallDTO.getMsgId());
    }
    @Async
    @TransactionalEventListener(classes = RecallEvent.class,fallbackExecution = true)
    public void sendToAll(RecallEvent recallEvent) {
        pushService.sendPushMsg(WebSocketAdapter.buildRecallMsg(recallEvent.getRecallDTO()));
    }
}
