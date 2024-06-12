package com.hyh.mallchat.common.chat.service.cache;

import com.hyh.mallchat.common.chat.dao.GroupMemberDao;
import com.hyh.mallchat.common.chat.dao.RoomGroupDao;
import com.hyh.mallchat.common.chat.domain.entity.RoomGroup;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class GroupMemberCache {
    @Autowired
    private GroupMemberDao groupMemberDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberIdsByRoomId(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        List<Long> memberIdsByRoomId = groupMemberDao.getMemberIdsByRoomId(roomGroup.getId());
        return memberIdsByRoomId;
    }
    @CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }
}
