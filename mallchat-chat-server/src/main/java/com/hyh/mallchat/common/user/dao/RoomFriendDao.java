package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.common.domain.enums.RoomNormalOrNoEnum;
import com.hyh.mallchat.common.user.domain.entity.RoomFriend;
import com.hyh.mallchat.common.user.mapper.RoomFriendMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyh.mallchat.common.user.service.adapter.ChatAdapter;
import org.springframework.stereotype.Service;

import java.util.List;

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
        lambdaUpdate().eq(RoomFriend::getId, id).set(RoomFriend::getStatus, RoomNormalOrNoEnum.NORMAL.getType()).update();
    }


    public void destroyRoom(String roomKey) {
        lambdaUpdate().eq(RoomFriend::getRoomKey, roomKey).set(RoomFriend::getStatus, RoomNormalOrNoEnum.NO.getType()).update();
    }
}
