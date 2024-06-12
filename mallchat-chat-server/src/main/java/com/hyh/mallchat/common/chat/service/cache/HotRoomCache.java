package com.hyh.mallchat.common.chat.service.cache;

import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class HotRoomCache {
    public void refreshActiveTime(Long roomId, Long msgId, Date createTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) createTime.getTime());
    }
}
