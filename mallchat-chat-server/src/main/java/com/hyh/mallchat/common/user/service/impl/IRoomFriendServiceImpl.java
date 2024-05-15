package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.user.dao.RoomFriendDao;
import com.hyh.mallchat.common.user.domain.entity.RoomFriend;
import com.hyh.mallchat.common.user.service.IRoomFriendService;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IRoomFriendServiceImpl implements IRoomFriendService {
    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    public RoomFriend createFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend insert = ChatAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }
}
