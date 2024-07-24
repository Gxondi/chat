package com.hyh.mallchat.common.chat.service;

import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;

import java.util.List;

/**
 * <p>
 * 房间表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
public interface IRoomService {

    RoomFriend createFriendRoom(List<Long> list);

    void destroyFriendRoom(List<Long> list);

    RoomFriend getRoomByFriendUid(long friendUid, long uid);
}
