package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.user.domain.entity.RoomFriend;

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
}
