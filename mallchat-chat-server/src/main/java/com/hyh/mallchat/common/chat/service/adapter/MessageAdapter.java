package com.hyh.mallchat.common.chat.service.adapter;

import cn.hutool.core.bean.BeanUtil;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.MessageMark;
import com.hyh.mallchat.common.chat.domain.enums.MessageStatusEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.ChatMessageReq;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.hyh.mallchat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.hyh.mallchat.common.chat.domain.enums.MessageMarkTypeEnum;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;

import java.util.*;
import java.util.stream.Collectors;

public class MessageAdapter {
    public static final Integer CAN_CALLBACK_GAP_COUNT = 100;

    public static Message buildMsgSave(ChatMessageReq request, Long uid) {
        return Message.builder()
                .fromUid(uid)
                .roomId(request.getRoomId())
                .type(request.getMsgType())
                .status(MessageStatusEnum.NORMAL.getStatus())
                .build();
    }

    public static List<ChatMessageResp> buildMsgResp(List<Message> messages, List<MessageMark> messageMarks, Long receiveUid) {
        //分组，mark
        Map<Long, List<MessageMark>> markMap = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getMsgId));
        return messages.stream().map(message -> {
            ChatMessageResp chatMessageResp = new ChatMessageResp();
            chatMessageResp.setFromUsr(buildFromUser(message.getFromUid()));
            chatMessageResp.setMsg(buildMessage(message, markMap.getOrDefault(message.getId(), new ArrayList<>()), receiveUid));
            return chatMessageResp;
        }).sorted(Comparator.comparing(a->a.getMsg().getSendTime())).collect(Collectors.toList());//按照发送时间排序
    }

    private static ChatMessageResp.Message buildMessage(Message message, List<MessageMark> messageMarks, Long receiveUid) {
        ChatMessageResp.Message messageVo = new ChatMessageResp.Message();
        BeanUtil.copyProperties(message, messageVo);
        messageVo.setBody(message.getContent());
        messageVo.setSendTime(message.getCreateTime());
        AbstractMsgHandler<?> msgHandler = MsgHandlerFactory.getStrategyNoNull(message.getType());
        if (Objects.nonNull(msgHandler)) {
            messageVo.setBody(msgHandler.showMsg(message));
        }
        messageVo.setMessageMark(buildMsgMark(messageMarks, receiveUid));
        return messageVo;
    }

    private static ChatMessageResp.MessageMark buildMsgMark(List<MessageMark> messageMarks, Long receiveUid) {
        Map<Integer, List<MessageMark>> marks = messageMarks.stream().collect(Collectors.groupingBy(MessageMark::getType));
        List<MessageMark> like = marks.getOrDefault(MessageMarkTypeEnum.LIKE.getType(), new ArrayList<>());
        List<MessageMark> dislike = marks.getOrDefault(MessageMarkTypeEnum.DISLIKE.getType(), new ArrayList<>());
        ChatMessageResp.MessageMark mark = new ChatMessageResp.MessageMark();
        mark.setDislikeCount(dislike.size());
        mark.setLikeCount(like.size());
        //是否点赞
        mark.setUserLike(Optional.ofNullable(receiveUid).filter(uid -> like.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        mark.setUserDislike(Optional.ofNullable(receiveUid).filter(uid -> dislike.stream().anyMatch(a -> Objects.equals(a.getUid(), uid))).map(a -> YesOrNoEnum.YES.getStatus()).orElse(YesOrNoEnum.NO.getStatus()));
        return mark;
    }

    private static ChatMessageResp.UserInfo buildFromUser(Long fromUid) {
        ChatMessageResp.UserInfo userInfo = new ChatMessageResp.UserInfo();
        userInfo.setUid(fromUid);
        return userInfo;
    }
}
