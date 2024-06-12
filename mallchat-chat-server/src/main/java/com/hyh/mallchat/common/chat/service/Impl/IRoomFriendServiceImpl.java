package com.hyh.mallchat.common.chat.service.Impl;

import com.hyh.mallchat.common.chat.dao.RoomFriendDao;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
import com.hyh.mallchat.common.chat.service.IRoomFriendService;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
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
