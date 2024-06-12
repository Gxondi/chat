package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.chat.mapper.RoomMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 房间表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class RoomDao extends ServiceImpl<RoomMapper, Room> {


    public void refreshLastMsg(Long roomId, Long msgId, Date createTime) {
        lambdaUpdate().eq(Room::getId, roomId)
                .set(Room::getLastMsgId, msgId)
                .set(Room::getActiveTime, createTime)
                .update();
    }
}
