package com.hyh.mallchat.common.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyh.mallchat.common.chat.domain.entity.MessageMark;
import com.hyh.mallchat.common.chat.mapper.MessageMarkMapper;
import com.hyh.mallchat.common.common.domain.enums.NormalOrNoEnum;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-15
 */
@Service
public class MessageMarkDao extends ServiceImpl<MessageMarkMapper, MessageMark> {

    public List<MessageMark> getMsgRespBatch(List<Long> msgIds) {
        return lambdaQuery()
                .in(MessageMark::getMsgId, msgIds)
                .eq(MessageMark::getStatus, NormalOrNoEnum.NORMAL.getStatus())
                .list();

    }

    public MessageMark get(Long uid, Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getUid,uid)
                .eq(MessageMark::getType,markType)
                .one();
    }

    public Integer getCount(Long msgId, Integer markType) {
        return lambdaQuery()
                .eq(MessageMark::getMsgId, msgId)
                .eq(MessageMark::getType,markType)
                .count();
    }
}
