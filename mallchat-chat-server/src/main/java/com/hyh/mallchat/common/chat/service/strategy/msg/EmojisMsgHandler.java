package com.hyh.mallchat.common.chat.service.strategy.msg;

import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.msg.EmojisMsgDTO;
import com.hyh.mallchat.common.chat.domain.entity.msg.MessageExtra;
import com.hyh.mallchat.common.chat.domain.entity.msg.MsgRecall;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class EmojisMsgHandler extends AbstractMsgHandler<EmojisMsgDTO> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserCache userCache;
    @Override
    protected MessageTypeEnum getMsgTypeEnum() {
        return MessageTypeEnum.IMAGE;
    }

    @Override
    public void checkMsg(EmojisMsgDTO body, Long roomId, Long uid) {

    }

    @Override
    public void saveMsg(Message message, EmojisMsgDTO body) {
        MessageExtra extra = Optional.ofNullable(message.getExtra()).orElse(new MessageExtra());
        Message update = new Message();
        update.setId(message.getId());
        update.setExtra(extra);
        extra.setEmojisMsgDTO(body);
        messageDao.updateById(update);
    }

    @Override
    public Object showMsg(Message msg) {
        MsgRecall recall = msg.getExtra().getRecall();
        User userInfo = userCache.getUserInfo(recall.getRecallUid());
        if(!Objects.equals(recall.getRecallUid(), msg.getFromUid())){
            return "管理员\"" + userInfo.getName() + "\"撤回了一条成员消息";
        }
        return  "\"" + userInfo.getName() + "\"撤回了一条成员消息";
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






























