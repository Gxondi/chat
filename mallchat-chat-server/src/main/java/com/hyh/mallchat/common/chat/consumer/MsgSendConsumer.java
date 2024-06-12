package com.hyh.mallchat.common.chat.consumer;

import com.hyh.mallchat.common.chat.dao.*;
import com.hyh.mallchat.common.chat.domain.dto.MsgSendMessageDTO;
import com.hyh.mallchat.common.chat.domain.entity.Contact;
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
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.impl.PushService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RocketMQMessageListener(consumerGroup = MQConstant.SEND_MSG_GROUP, topic = MQConstant.SEND_MSG_TOPIC)
@Component
public class MsgSendConsumer implements RocketMQListener<MsgSendMessageDTO> {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private PushService pushService;
    @Autowired
    private RoomFriendDao roomFriendDao;

    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private GroupMemberCache groupMemberCache;
    @Override
    public void onMessage(MsgSendMessageDTO msgSendMessageDTO) {
        Long msgId = msgSendMessageDTO.getMsgId();
        //查出消息
        Message message = messageDao.getById(msgId);
        //查出房间
        Room room = roomCache.get(message.getRoomId());
        ChatMessageResp chatMessageResp = chatService.getMsgResp(message, null);
        //更新房间最新消息
        roomDao.refreshLastMsg(room.getId(), message.getId(), message.getCreateTime());
        //删除房间缓存
        roomCache.delete(room.getId());
        if(room.isHotRoom()){
            //更新热门房间时间
            hotRoomCache.refreshActiveTime(room.getId(), message.getId(), message.getCreateTime());
            //推送给所有在线用户
            pushService.sendAllOnlineUser(WebSocketAdapter.buildMsgSend(chatMessageResp));
        }else {
            List<Long> memeberList = new ArrayList<>();
            if(Objects.equals(room.getType(), RoomTypeEnum.GROUP.getType())){
                memeberList = groupMemberCache.getMemberIdsByRoomId(room.getId());

            }else if(Objects.equals(room.getType(), RoomTypeEnum.FRIEND.getType())){
                RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
                memeberList = Arrays.asList(roomFriend.getUid1(), roomFriend.getUid2());
            }
            contactDao.refreshActiveTime(room.getId(),memeberList, message.getId(), message.getCreateTime());
            pushService.sendGroupMsg(WebSocketAdapter.buildMsgSend(chatMessageResp), memeberList);
        }
    }
}
