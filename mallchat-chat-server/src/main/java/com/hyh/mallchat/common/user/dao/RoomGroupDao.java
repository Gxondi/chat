package com.hyh.mallchat.common.user.dao;

import com.hyh.mallchat.common.user.domain.entity.RoomGroup;
import com.hyh.mallchat.common.user.mapper.RoomGroupMapper;
import com.hyh.mallchat.common.user.service.IRoomGroupService;
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

}
