package com.hyh.mallchat.common.chat.service.cache;

import com.hyh.mallchat.common.chat.dao.RoomDao;
import com.hyh.mallchat.common.chat.dao.RoomGroupDao;
import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.chat.domain.entity.RoomGroup;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomGroupCache extends AbstractRedisStringCache<Long, RoomGroup> {
    @Autowired
    private RoomGroupDao roomGroupDao;

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, uid);
    }

    @Override
    protected Map<Long, RoomGroup> load(List<Long> uid) {
        List<RoomGroup> roomGroups = roomGroupDao.listByIds(uid);
        return  roomGroups.stream().collect(Collectors.toMap(RoomGroup::getId, Function.identity()));
    }
}
