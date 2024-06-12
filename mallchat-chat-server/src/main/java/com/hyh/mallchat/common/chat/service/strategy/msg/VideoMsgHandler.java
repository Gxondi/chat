package com.hyh.mallchat.common.chat.service.strategy.msg;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.hyh.mallchat.common.chat.domain.entity.msg.VideoMsgDTO;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.msg.TextMsgReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VideoMsgHandler extends AbstractMsgHandler<VideoMsgDTO> {
    @Autowired
    private MessageDao messageDao;
    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.VIDEO;
    }

    @Override
    public void checkMsg(VideoMsgDTO body, Long roomId, Long uid) {

    }

    @Override
    public void saveMsg(Message message, VideoMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(extra);
        extra.setVideoMsgDTO(body);
        messageDao.updateById(update);
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






























