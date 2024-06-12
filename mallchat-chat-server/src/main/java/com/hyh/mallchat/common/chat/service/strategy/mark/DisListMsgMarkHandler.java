package com.hyh.mallchat.common.chat.service.strategy.mark;

import com.hyh.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

@Component
public class DisListMsgMarkHandler extends AbstractMsgMarkHandler{

    @Override
    protected MessageMarkTypeEnum getMarkType() {
        return MessageMarkTypeEnum.DISLIKE;
    }

    @Override
    public void doMark(Long uid, Long msgId) {
        super.doMark(uid, msgId);
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid,msgId);

    }
}
