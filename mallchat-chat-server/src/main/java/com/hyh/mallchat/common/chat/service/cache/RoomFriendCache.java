package com.hyh.mallchat.common.chat.service.cache;

import com.hyh.mallchat.common.chat.dao.RoomFriendDao;
import com.hyh.mallchat.common.chat.dao.RoomGroupDao;
import com.hyh.mallchat.common.chat.domain.entity.RoomFriend;
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
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Autowired
    private RoomFriendDao roomFriendDao;

    @Override
    protected Long getExpireSeconds() {
        return 5*60L;
    }

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.GROUP_INFO_STRING, uid);
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> uid) {
        List<RoomFriend> roomFriendList = roomFriendDao.listByIds(uid);
        return roomFriendList.stream().collect(Collectors.toMap(RoomFriend::getId, Function.identity()));
    }
}
