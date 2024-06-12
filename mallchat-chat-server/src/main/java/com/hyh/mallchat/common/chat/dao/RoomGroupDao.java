package com.hyh.mallchat.common.chat.dao;

import com.hyh.mallchat.common.chat.domain.entity.RoomGroup;
import com.hyh.mallchat.common.chat.mapper.RoomGroupMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 群聊房间表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class RoomGroupDao extends ServiceImpl<RoomGroupMapper, RoomGroup> {

    public RoomGroup getByRoomId(Long id) {
        return lambdaQuery().eq(RoomGroup::getId, id).one();
    }
}
