package com.hyh.mallchat.common.chat.service.cache;

import com.hyh.mallchat.common.chat.dao.RoomDao;
import com.hyh.mallchat.common.chat.domain.entity.Room;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.service.cache.AbstractRedisStringCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    @Autowired
    private RoomDao roomDao;

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, uid);
    }

    @Override
    protected Map<Long, Room> load(List<Long> uid) {
        List<Room> rooms = roomDao.listByIds(uid);
        return  rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
