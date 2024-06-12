package com.hyh.mallchat.common.chat.service.strategy.mark;

import com.hyh.mallchat.common.chat.dao.MessageMarkDao;
import com.hyh.mallchat.common.chat.domain.dto.ChatMessageMarkDTO;
import com.hyh.mallchat.common.chat.domain.entity.MessageMark;
import com.hyh.mallchat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.hyh.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.common.event.MessageMarkEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractMsgMarkHandler {
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private ApplicationEventPublisher publisher;

    /**
     * 抛出去让子类实现
     *
     * @return
     */
    protected abstract MessageMarkTypeEnum getMarkType();

    @PostConstruct
    private void init() {
        MsgMarkFactory.register(this, getMarkType().getType());
    }

    /**
     * 喜欢标记/取消标记
     */
    @Transactional
    public void doMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.MARK);
    }

    private void exec(Long uid, Long msgId, MessageMarkActTypeEnum actTypeEnum) {
        /**
         *   MARK(1,"确认标记"),
         *   UNMARK(2,"取消标记");
         */
        Integer actType = actTypeEnum.getType();
        /**
         * 喜欢/不喜欢
         */
        Integer markType = getMarkType().getType();
        MessageMark oldMark = messageMarkDao.get(uid, msgId, markType);
        if (Objects.isNull(oldMark) && actTypeEnum == MessageMarkActTypeEnum.UNMARK) {
            return;
        }
        MessageMark insertOrUpdate = MessageMark.builder()
                .id(Optional.ofNullable(oldMark).map(MessageMark::getId).orElse(null))
                .type(markType)
                .msgId(msgId)
                .uid(uid)
                .status(actType == 1 ? YesOrNoEnum.YES.getStatus() : YesOrNoEnum.NO.getStatus())
                .build();
        boolean saveOrUpdate = messageMarkDao.saveOrUpdate(insertOrUpdate);
        if(saveOrUpdate){
            ChatMessageMarkDTO dto = ChatMessageMarkDTO.builder()
                    .uid(uid)
                    .msgId(msgId)
                    .actType(actType)
                    .markType(markType)
                    .build();
            publisher.publishEvent(new MessageMarkEvent(this,dto));
        }
    }

    @Transactional
    public void unMark(Long uid, Long msgId) {
        exec(uid, msgId, MessageMarkActTypeEnum.UNMARK);
    }
}
