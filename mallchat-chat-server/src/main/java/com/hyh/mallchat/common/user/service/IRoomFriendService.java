package com.hyh.mallchat.common.user.service;

import com.hyh.mallchat.common.user.domain.entity.RoomFriend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 单聊房间表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
public interface IRoomFriendService {


    RoomFriend createFriendRoom(Long id, List<Long> uidList);
}
