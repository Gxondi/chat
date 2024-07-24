package com.hyh.mallchat.common.chat.service.Impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.hyh.mallchat.common.chat.dao.*;
import com.hyh.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.hyh.mallchat.common.chat.domain.entity.*;
import com.hyh.mallchat.common.chat.domain.enums.MessageMarkActTypeEnum;
import com.hyh.mallchat.common.chat.domain.enums.MessageTypeEnum;
import com.hyh.mallchat.common.chat.domain.vo.req.*;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageReadResp;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatMessageResp;
import com.hyh.mallchat.common.chat.service.ChatService;
import com.hyh.mallchat.common.chat.service.IContactService;
import com.hyh.mallchat.common.chat.service.adapter.MessageAdapter;
import com.hyh.mallchat.common.chat.service.cache.RoomCache;
import com.hyh.mallchat.common.chat.service.cache.RoomGroupCache;
import com.hyh.mallchat.common.chat.service.strategy.mark.AbstractMsgMarkHandler;
import com.hyh.mallchat.common.chat.service.strategy.mark.MsgMarkFactory;
import com.hyh.mallchat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.hyh.mallchat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.hyh.mallchat.common.chat.service.strategy.msg.RecallMsgHandler;
import com.hyh.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.event.MessageSendEvent;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private MsgHandlerFactory msgHandlerFactory;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private MessageMarkDao messageMarkDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private IRoleService iRoleService;
    @Autowired
    private RecallMsgHandler recallMsgHandler;
    @Autowired
    private IContactService contactService;

    @Override
    @Transactional
    public Long sendMsg(ChatMessageReq request, Long uid) {
        check(request,uid);
        //策略模式判断消息类型
        AbstractMsgHandler<?> msgHandler = msgHandlerFactory.getStrategyNoNull(request.getMsgType());
        Long msgId = msgHandler.checkAndSendMsg(request, uid);
        //TODO 发送消息 MQ
        applicationEventPublisher.publishEvent(new MessageSendEvent(this,msgId));
        return msgId;
    }

    @Override
    public ChatMessageResp getMsgResp(Message message, Long receiveUid) {
        return CollUtil.getFirst(getMsgRespBatch(Collections.singletonList(message), receiveUid));
    }

    @Override
    public CursorPageBaseResp<ChatMessageResp> getMsgPage(ChatMessagePageReq request, Long uid) {
        //用最后一条消息id，来限制被踢出的人能看见的最大一条消息
        Long lastMsgId = getLastMsg(request.getRoomId(),uid);
        CursorPageBaseResp<Message> cursorPage = messageDao.getCursorPage(request.getRoomId(), request, lastMsgId);
        return CursorPageBaseResp.init(cursorPage,getMsgRespBatch(cursorPage.getList(), uid));
    }

    @Override
    public void recallMsg(ChatMessageBaseReq request, Long uid) {
        Message message = messageDao.getById(request.getMsgId());
        checkRecall(uid,message);
        recallMsgHandler.recall(uid,message);
    }

    @Override
    public void setMsgMark(Long uid, ChatMessageMarkReq req) {
        //获取动作类型
        Integer markType = req.getMarkType();
        //喜欢/不喜欢
        AbstractMsgMarkHandler strategyNoNull = MsgMarkFactory.getStrategyNoNull(markType);
        //动作枚举
        MessageMarkActTypeEnum messageMarkActTypeEnum = MessageMarkActTypeEnum.of(req.getActType());
        switch (messageMarkActTypeEnum){
            case MARK:
                strategyNoNull.doMark(uid, req.getMsgId());
                break;
            case UNMARK:
                strategyNoNull.unMark(uid, req.getMsgId());
                break;
        }

    }

    @Override
    public void readMsg(Long uid, ChatMessageMemberReq req) {
        Long roomId = req.getRoomId();
        Contact contact = contactDao.get(uid, roomId);
        if(Objects.nonNull(contact)){
            Contact update = new Contact();
            update.setUid(uid);
            update.setRoomId(roomId);
            update.setReadTime(new Date());
            contactDao.updateById(update);
        }else {
            Contact insert = new Contact();
            insert.setRoomId(roomId);
            insert.setUid(uid);
            insert.setReadTime(new Date());
            contactDao.save(insert);
        }

    }

    @Override
    public CursorPageBaseResp<ChatMessageReadResp> getReadOrUnReadPage(Long uid, ChatMessageReadReq req) {
        Long searchType = req.getSearchType();
        Long msgId = req.getMsgId();
        //查看次消息的已读未读
        Message message = messageDao.getById(msgId);
        AssertUtil.isNotEmpty(message,"消息有误");
        AssertUtil.equal(uid,message.getFromUid(),"只能查看自己的消息");
        CursorPageBaseResp<Contact> page;
        if(searchType == 1){
            page = contactDao.getReadPage(message,req);
        }else {
            page = contactDao.getUnReadPage(message,req);
        }
        return CursorPageBaseResp.init(page, ChatAdapter.buildMsgRead(page.getList()));
    }

    @Override
    public Collection<MsgReadInfoDTO> readOrUnReadCount(Long uid, ChatMessageReadInfoReq req) {
        List<Long> msgIds = req.getMsgIds();
        List<Message> messages = messageDao.listByIds(msgIds);
        messages.forEach(message -> AssertUtil.equal(message.getFromUid(),uid,"只能查询自己的消息"));
        return contactService.getMsgReadInfo(messages).values();

    }

    @Override
    public void groupInfo(Long uid, long id) {

    }

    private void checkRecall(Long uid, Message message) {
        AssertUtil.isNotEmpty(message, "消息不存在");
        AssertUtil.notEqual(message.getType(), MessageTypeEnum.RECALL.getType(), "消息无法撤回");
        boolean hasPower = iRoleService.hasPower(uid, RoleEnum.CHAT_MANAGER);
        if (hasPower){
            return;
        }
        boolean self = Objects.equals(uid, message.getFromUid());
        AssertUtil.isTrue(self, "没有权限");
        long between = DateUtil.between(message.getCreateTime(), new Date(), DateUnit.MINUTE);
        AssertUtil.isTrue(between<2, "超过2分钟无法撤回");
    }

    private Long getLastMsg(Long roomId, Long uid) {
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        if (room.isHotRoom()) {
            return null;
        }
        AssertUtil.isNotEmpty(uid, "请先登录");
        Contact contact = contactDao.get(uid, roomId);
        return contact.getLastMsgId();
    }


    private List<ChatMessageResp> getMsgRespBatch(List<Message> messages, Long receiveUid) {
        if (CollUtil.isEmpty(messages)){
            return Collections.emptyList();
        }
        List<MessageMark> messageMarks = messageMarkDao.getMsgRespBatch(messages.stream().map(Message::getId).collect(Collectors.toList()));

        return MessageAdapter.buildMsgResp(messages, messageMarks, receiveUid);
    }

    private void check(ChatMessageReq request, Long uid) {
        Room room = roomCache.get(request.getRoomId());
        if(room.isHotRoom()){
            return;
        }
        if (room.isFriendRoom()){
            RoomFriend roomFriend = roomFriendDao.getByRoomId(room.getId());
            AssertUtil.equal(roomFriend.getStatus(), NormalOrNoEnum.NO.getStatus(), "您已经被拉黑");
            AssertUtil.isTrue(uid.equals(roomFriend.getUid1()) || uid.equals(roomFriend.getUid2()), "您已经被拉黑");
        }
        if (room.isGroupRoom()){
            RoomGroup roomGroup = roomGroupCache.get(room.getId());
            GroupMember groupMember = groupMemberDao.getGroupMember(roomGroup.getId(), uid);
            AssertUtil.isNotEmpty(groupMember, "您已经被提出群聊");
        }

    }
}
