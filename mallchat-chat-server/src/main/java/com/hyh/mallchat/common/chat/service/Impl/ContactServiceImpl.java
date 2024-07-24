package com.hyh.mallchat.common.chat.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.hyh.mallchat.common.chat.dao.ContactDao;
import com.hyh.mallchat.common.chat.dao.MessageDao;
import com.hyh.mallchat.common.chat.domain.dto.MsgReadInfoDTO;
import com.hyh.mallchat.common.chat.domain.dto.RoomBaseInfo;
import com.hyh.mallchat.common.chat.domain.entity.*;
import com.hyh.mallchat.common.chat.domain.vo.resp.ChatRoomResp;
import com.hyh.mallchat.common.chat.service.IContactService;
import com.hyh.mallchat.common.chat.service.IRoomService;
import com.hyh.mallchat.common.chat.service.cache.HotRoomCache;
import com.hyh.mallchat.common.chat.service.cache.RoomCache;
import com.hyh.mallchat.common.chat.service.cache.RoomFriendCache;
import com.hyh.mallchat.common.chat.service.cache.RoomGroupCache;
import com.hyh.mallchat.common.chat.service.strategy.msg.AbstractMsgHandler;
import com.hyh.mallchat.common.chat.service.strategy.msg.MsgHandlerFactory;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.req.FriendReqVo;
import com.hyh.mallchat.common.common.domain.vo.req.IdReqVO;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import com.hyh.mallchat.common.user.service.cache.UserInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements IContactService {
    @Autowired
    private HotRoomCache hotRoomCache;
    @Autowired
    private ContactDao contactDao;
    @Autowired
    private RoomCache roomCache;
    @Autowired
    private RoomGroupCache roomGroupCache;
    @Autowired
    private UserInfoCache userInfoCache;
    @Autowired
    private RoomFriendCache roomFriendCache;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private IRoomService roomService;

    @Override
    public CursorPageBaseResp<ChatRoomResp> getContactPage(Long uid, CursorPageBaseReq cursorReq) {
        //获取uid的全部会话列表
        CursorPageBaseResp<Long> page;
        //分为登录态/未登录态
        if (Objects.nonNull(uid)) {
            Double hotStart = null;
            Double hotEnd = getCursorOrNull(cursorReq.getCursor());
            //普通会话列表
            CursorPageBaseResp<Contact> contactCursorPage = contactDao.getPage(uid, cursorReq);
            List<Long> baseRoomIds = contactCursorPage.getList()
                    .stream()
                    .map(Contact::getRoomId)
                    .collect(Collectors.toList());
            if (!contactCursorPage.getIsLast()) {
                hotStart = getCursorOrNull(contactCursorPage.getCursor());
            }
            //从热点群里缓存中取值列表游标分页
            Set<ZSetOperations.TypedTuple<String>> roomRange = hotRoomCache.getRoomRange(hotStart, hotEnd);
            List<Long> hotRoomIds = roomRange.stream().map(ZSetOperations.TypedTuple::getValue).filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toList());
            baseRoomIds.addAll(hotRoomIds);
            page = CursorPageBaseResp.init(contactCursorPage, baseRoomIds);
        } else {
            //未登录
            //从热点群里缓存中取值列表游标分页
            CursorPageBaseResp<Pair<Long, Double>> roomCursorPage = hotRoomCache.getRoomCursorPage(cursorReq);
            List<Long> roomIds = roomCursorPage.getList().stream().map(Pair::getKey).collect(Collectors.toList());
            page = CursorPageBaseResp.init(roomCursorPage, roomIds);
        }
        if (CollectionUtil.isEmpty(page.getList())) {
            return CursorPageBaseResp.empty();
        }

        List<ChatRoomResp> chatRoomRespList = buildContactResp(uid, page.getList());

        return CursorPageBaseResp.init(page, chatRoomRespList);
    }

    @Override
    public ChatRoomResp getDetail(Long uid, IdReqVO reqVO) {
        long roomId = reqVO.getId();
        Room room = roomCache.get(roomId);
        AssertUtil.isNotEmpty(room, "房间号有误");
        List<ChatRoomResp> chatRoomRespList = buildContactResp(uid, Collections.singletonList(roomId));
        return chatRoomRespList.get(0);
    }

    @Override
    public ChatRoomResp getContactDetailByFriend(Long uid, FriendReqVo reqVO) {
        long friendUid = reqVO.getUid();
        RoomFriend friendRoom = roomService.getRoomByFriendUid(uid, friendUid);
        AssertUtil.isNotEmpty(friendRoom,"不是您的好友");
        return buildContactResp(uid, Collections.singletonList(friendUid)).get(0);
    }

    @Override
    public Map<Long, MsgReadInfoDTO> getMsgReadInfo(List<Message> messages) {
        Map<Long, List<Message>> roomGroup = messages.stream().collect(Collectors.groupingBy(Message::getRoomId));
        AssertUtil.equal(roomGroup.size(), 1, "只能查相同房间下的消息");
        Long roomId = roomGroup.keySet().iterator().next();
        Integer total = contactDao.getTotal(roomId);

        return messages.stream().map(message -> {
            MsgReadInfoDTO dto = new MsgReadInfoDTO();
            Integer readCount = contactDao.getReadCount(message);
            Integer UnreadCount = total - 1 - readCount;
            dto.setMsgId(message.getId());
            dto.setUnReadCount(UnreadCount);
            dto.setReadCount(readCount);
            return dto;
        }).collect(Collectors.toMap(MsgReadInfoDTO::getMsgId,Function.identity()));
    }


    @NotNull
    private List<ChatRoomResp> buildContactResp(Long uid, List<Long> roomIds) {
        /**
         *   @ApiModelProperty("房间id")
         *     private Long roomId;
         *     @ApiModelProperty("房间类型 1群聊 2单聊")
         *     private Integer type;
         *     @ApiModelProperty("是否全员展示的会话 0否 1是")
         *     private Integer hot_Flag;
         *     @ApiModelProperty("最新消息")
         *     private String text;
         *     @ApiModelProperty("会话名称")
         *     private String name;
         *     @ApiModelProperty("会话头像")
         *     private String avatar;
         *     @ApiModelProperty("房间最后活跃时间(用来排序)")
         *     private Date activeTime;
         *     @ApiModelProperty("未读数")
         *     private Integer unreadCount;
         */

        Map<Long, RoomBaseInfo> roomBaseInfoMap = getRoomBaseInfoMap(uid, roomIds);
        //最后一条消息
        List<Long> msgId = roomBaseInfoMap.values().stream().map(RoomBaseInfo::getLastMsgId).collect(Collectors.toList());
        List<Message> messageList = CollectionUtil.isEmpty(msgId) ? new ArrayList<>() : messageDao.listByIds(msgId);
        Map<Long, Message> messageMap = messageList.stream().collect(Collectors.toMap(Message::getId, Function.identity()));
        //最后一条消息用户列表
        Map<Long, User> lastMsgUserMap = userInfoCache.getBatch(messageMap.values().stream().map(Message::getFromUid).collect(Collectors.toList()));

        //未读数
        Map<Long, Integer> unReadCountMap = getUnReadCountMap(uid, roomIds);
        return roomBaseInfoMap.values().stream()
                .map(room -> {
                    ChatRoomResp chatRoomResp = new ChatRoomResp();
                    RoomBaseInfo roomBaseInfo = roomBaseInfoMap.get(room.getRoomId());
                    chatRoomResp.setAvatar(roomBaseInfo.getAvatar());
                    chatRoomResp.setRoomId(room.getRoomId());
                    chatRoomResp.setActiveTime(room.getActiveTime());
                    chatRoomResp.setHot_Flag(roomBaseInfo.getHotFlag());
                    chatRoomResp.setType(roomBaseInfo.getType());
                    chatRoomResp.setName(roomBaseInfo.getName());
                    Message message = messageMap.get(room.getLastMsgId());
                    if (!Objects.isNull(message)) {
                        AbstractMsgHandler strategyNoNull = MsgHandlerFactory.getStrategyNoNull(message.getType());
                        chatRoomResp.setText(lastMsgUserMap.get(message.getFromUid()).getName() + ":" + strategyNoNull.showContactMsg(message));
                    }
                    chatRoomResp.setUnreadCount(unReadCountMap.getOrDefault(room.getRoomId(), 0));
                    return chatRoomResp;
                }).collect(Collectors.toList());
    }

    private Map<Long, Integer> getUnReadCountMap(Long uid, List<Long> roomIds) {
        if (Objects.isNull(uid)) {
            return new HashMap<>();
        }
        List<Contact> contacts = contactDao.getByRoomIds(uid, roomIds);
        return contacts.parallelStream()
                .map(contact -> Pair.of(contact.getRoomId(), messageDao.getUnReadCount(contact.getRoomId(), contact.getReadTime())))
                .collect(Collectors.toMap(Pair::getKey,Pair::getValue));
    }
    private Map<Long, RoomBaseInfo> getRoomBaseInfoMap(Long uid, List<Long> roomIds) {
        Map<Long, Room> roomMap = roomCache.getBatch(roomIds);
        Map<Integer, List<Long>> roomGroupByType = roomMap.values().stream().collect(Collectors.groupingBy(Room::getType,
                Collectors.mapping(Room::getId, Collectors.toList())));

        List<Long> groupRooms = roomGroupByType.get(RoomTypeEnum.GROUP.getType());
        Map<Long, RoomGroup> groupRoomMap = roomGroupCache.getBatch(groupRooms);

        List<Long> friendRooms = roomGroupByType.get(RoomTypeEnum.FRIEND.getType());
        Map<Long, User> friendRoomMap = getFriendRoomMap(friendRooms, uid);

        return roomMap.values().stream()
                .map(room -> {
                    RoomBaseInfo roomBaseInfo = new RoomBaseInfo();
                    roomBaseInfo.setRoomId(room.getId());
                    roomBaseInfo.setType(room.getType());
                    roomBaseInfo.setHotFlag(room.getHotFlag());
                    roomBaseInfo.setLastMsgId(room.getLastMsgId());
                    roomBaseInfo.setActiveTime(room.getActiveTime());
                    if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.GROUP) {
                        RoomGroup roomGroup = groupRoomMap.get(room.getId());
                        roomBaseInfo.setName(roomGroup.getName());
                        roomBaseInfo.setAvatar(roomGroup.getAvatar());
                    } else if (RoomTypeEnum.of(room.getType()) == RoomTypeEnum.FRIEND) {
                        User user = friendRoomMap.get(room.getId());
                        roomBaseInfo.setName(user.getName());
                        roomBaseInfo.setAvatar(user.getAvatar());
                    }
                    return roomBaseInfo;
                }).collect(Collectors.toMap(RoomBaseInfo::getRoomId, Function.identity()));
    }

    private Map<Long, User> getFriendRoomMap(List<Long> roomIds, Long uid) {
        if (CollectionUtil.isEmpty(roomIds)) {
            return new HashMap<>();
        }
        Map<Long, RoomFriend> friendMap = roomFriendCache.getBatch(roomIds);
        List<Long> friendUidSet = ChatAdapter.buildFriendUidSet(friendMap.values(), uid);
        Map<Long, User> userMap = userInfoCache.getBatch(friendUidSet);

        return friendMap.values().stream().collect(Collectors.toMap(RoomFriend::getRoomId, roomFriend -> {
            Long friendUid = ChatAdapter.getFriendUid(roomFriend, uid);
            return userMap.get(friendUid);
        }));
    }

    private Double getCursorOrNull(String cursor) {
        return Optional.ofNullable(cursor)
                .map(Double::valueOf)
                .orElse(null);
    }
}
