package com.hyh.mallchat.common.chat.service.Impl;

import com.hyh.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.hyh.mallchat.common.common.domain.enums.RoomTypeEnum;
import com.hyh.mallchat.common.common.utils.AssertUtil;
import com.hyh.mallchat.common.chat.dao.RoomDao;
import com.hyh.mallchat.common.chat.dao.RoomFriendDao;
import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
import com.hyh.mallchat.common.chat.service.IRoomFriendService;
import com.hyh.mallchat.common.chat.service.IRoomService;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class IRoomServiceImpl implements IRoomService {
    @Autowired
    private RoomFriendDao roomFriendDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private IRoomFriendService roomFriendService;

    @Override
    @Transactional
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "用户id不能为空");
        AssertUtil.equal(uidList.size(), 2, "用户id数量不正确");

        String roomKey = ChatAdapter.generateRoomKey(uidList);
        RoomFriend roomFriend = roomFriendDao.getRoomKey(roomKey);
        //如果已经存在房间则复原
        if (Objects.nonNull(roomFriend)) {
            restartRoomFriend(roomFriend);
        } else {//不存在则创建
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = roomFriendService.createFriendRoom(room.getId(), uidList);
        }
        return roomFriend;
    }

    @Override
    public void destroyFriendRoom(List<Long> list) {
        AssertUtil.isNotEmpty(list, "用户id不能为空");
        AssertUtil.equal(list.size(), 2, "用户id数量不正确");
        //验证key
        String roomKey = ChatAdapter.generateRoomKey(list);
        roomFriendDao.destroyRoom(roomKey);
    }

    private Room createRoom(RoomTypeEnum roomTypeEnum) {
        Room insert = ChatAdapter.buildRoom(roomTypeEnum);
        roomDao.save(insert);
        return insert;
    }

    private void restartRoomFriend(RoomFriend hasRoomFriend) {
        if (Objects.equals(hasRoomFriend.getStatus(), NormalOrNoEnum.NO.getStatus())) {
            roomFriendDao.restartRoom(hasRoomFriend.getId());
        }
    }
}
