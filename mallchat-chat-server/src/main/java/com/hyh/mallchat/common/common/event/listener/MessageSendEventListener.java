package com.hyh.mallchat.common.common.event.listener;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.dto.MsgSendMessageDTO;
import com.hyh.mallchat.common.common.constant.MQConstant;
import com.hyh.mallchat.common.common.event.MessageSendEvent;
import com.hyh.mallchat.transaction.service.MQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageSendEventListener{
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private MQProducer mqProducer;
    @Async
    @TransactionalEventListener(classes = MessageSendEvent.class, phase = TransactionPhase.BEFORE_COMMIT)
    public void messageRoute(MessageSendEvent messageSendEvent) {
       Long msgId = messageSendEvent.getMsgId();

       mqProducer.sendSecureMsg(MQConstant.SEND_MSG_TOPIC, new MsgSendMessageDTO(msgId), msgId);
    }
}
