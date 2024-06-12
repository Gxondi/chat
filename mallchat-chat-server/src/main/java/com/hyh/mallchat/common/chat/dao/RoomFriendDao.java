package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.common.domain.enums.NormalOrNoEnum;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
import com.hyh.mallchat.common.chat.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 单聊房间表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class RoomFriendDao extends ServiceImpl<RoomFriendMapper, RoomFriend> {

    public RoomFriend getRoomKey(String roomKey) {
        return lambdaQuery().eq(RoomFriend::getRoomKey, roomKey).one();
    }

    public void restartRoom(Long id) {
        lambdaUpdate().eq(RoomFriend::getId, id).set(RoomFriend::getStatus, NormalOrNoEnum.NORMAL.getStatus()).update();
    }


    public void destroyRoom(String roomKey) {
        lambdaUpdate().eq(RoomFriend::getRoomKey, roomKey).set(RoomFriend::getStatus, NormalOrNoEnum.NO.getStatus()).update();
    }

    public RoomFriend getByRoomId(Long id) {
       return lambdaQuery().eq(RoomFriend::getId, id).one();
    }
}
