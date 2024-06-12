package com.hyh.mallchat.common.chat.service.strategy.msg;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.hyh.mallchat.common.chat.domain.entity.msg.MsgRecall;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.msg.TextMsgReq;
import com.hyh.mallchat.common.common.event.RecallEvent;
import com.hyh.mallchat.common.user.domain.dto.MsgRecallDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
public class RecallMsgHandler extends AbstractMsgHandler<Object> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.RECALL;
    }

    @Override
    public void checkMsg(Object body, Long roomId, Long uid) {

    }

    @Override
    public void saveMsg(Message message, Object body) {
        throw new UnsupportedOperationException();
    }

    public void recall(Long recallUid, Message message) {
        MessageExtra extra = message.getExtra();
        extra.setRecall(new MsgRecall(recallUid, new Date()));
        Message update = new Message();
        update.setId(message.getId());
        update.setType(MessageTypeEnum.RECALL.getType());
        update.setExtra(extra);
        messageDao.updateById(update);
        publisher.publishEvent(new RecallEvent(this,new MsgRecallDTO(message.getId(), message.getRoomId(), recallUid)));
    }

    @Override
    public Object showMsg(Message msg) {
        return null;
    }

    @Override
    public Object showReplyMsg(Message msg) {
        return null;
    }

    @Override
    public String showContactMsg(Message msg) {
        return null;
    }


    //    @Override
//    public void checkMsg(ChatMessageReq request, Long uid) {
//        TextMsgHandler textMsgHandler = BeanUtil.toBean(request.getBody(), TextMsgHandler.class);
//        AssertUtil.allCheckValidateThrow(textMsgHandler);
//    }
//    /**
//     * 根据策略不同保存消息到extra
//     * @param msg
//     * @param request
//     */
//    @Override
//    public void saveMsg(Message msg, ChatMessageReq request) {
//        //TextMsgHandler body = BeanUtil.toBean(request.getBody(), TextMsgHandler.class);
//        String body = toBean(request);
//        MessageExtra extra = Optional.ofNullable(msg.getExtra()).orElse(new MessageExtra());
//        Message update = new Message();
//        update.setId(msg.getId());
//        update.setExtra(extra);
//        update.setContent(body);
//        messageDao.updateById(update);
//    }
//
//
//    private String toBean(ChatMessageReq request) {
//        Object body = request.getBody();
//        return body.toString();
//    }

}






























