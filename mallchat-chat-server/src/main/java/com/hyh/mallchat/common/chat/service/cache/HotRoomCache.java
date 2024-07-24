package com.hyh.mallchat.common.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.domain.vo.req.CursorPageBaseReq;
import com.hyh.mallchat.common.common.domain.vo.resp.CursorPageBaseResp;
import com.hyh.mallchat.common.common.utils.CursorUtils;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
public class HotRoomCache {
    public void refreshActiveTime(Long roomId, Long msgId, Date createTime) {
        RedisUtils.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) createTime.getTime());
    }

    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq cursorReq) {
       return CursorUtils.getCursorPageByRedis(cursorReq,RedisKey.getKey(RedisKey.HOT_ROOM_ZET),Long::parseLong);
    }

    public Set<ZSetOperations.TypedTuple<String>> getRoomRange(Double hotStart, Double hotEnd) {
        return RedisUtils.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET),hotStart,hotEnd);
    }
}
