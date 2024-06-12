package com.hyh.mallchat.common.user.consumer;

import com.hyh.mallchat.common.chat.dao.*;
import com.hyh.mallchat.common.chat.domain.dto.MsgSendMessageDTO;
import com.hyh.mallchat.common.chat.domain.entity.Message;
import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.chat.service.ChatService;
import com.hyh.mallchat.common.chat.service.cache.GroupMemberCache;
import com.hyh.mallchat.common.chat.service.cache.HotRoomCache;
import com.hyh.mallchat.common.chat.service.cache.RoomCache;
import com.hyh.mallchat.common.common.constant.MQConstant;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import com.hyh.mallchat.common.common.domain.enums.WSPushTypeEnum;
import com.hyh.mallchat.common.user.service.impl.PushService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.common.wabsocket.domain.dto.PushMsgDTO;
import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RocketMQMessageListener(consumerGroup = MQConstant.PUSH_GROUP, topic = MQConstant.PUSH_TOPIC,messageModel = MessageModel.BROADCASTING)
@Component
public class PushConsumer implements RocketMQListener<PushMsgDTO> {
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void onMessage(PushMsgDTO pushMsgDTO) {
        Integer pushType = pushMsgDTO.getPushType();
        WSPushTypeEnum wsPushTypeEnum = WSPushTypeEnum.of(pushType);
        switch (wsPushTypeEnum){
            case USER:
                pushMsgDTO.getUidList().forEach(uid -> {
                    webSocketService.sendToUid(pushMsgDTO.getWsBaseMsg(),uid);
                });
                break;
            case ALL:
                webSocketService.sendToAllOnline(pushMsgDTO.getWsBaseMsg(),null);
                break;
        }

    }
}
