package com.hyh.mallchat.transaction.dao;

import cn.hutool.core.date.DateUtil;
import com.hyh.mallchat.transaction.domain.entity.SecureInvokeRecord;
import com.hyh.mallchat.transaction.mapper.SecureInvokeRecordMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyh.mallchat.transaction.service.SecureInvokeService;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 本地消息表 服务实现类
 * </p>
 *
 * @author CondiX
 * @since 2024-05-26
 */
@Component
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord>  {

    public List<SecureInvokeRecord> getWaitRetryRecords() {
        Date now = new Date();
        //查2分钟前的失败数据。避免刚入库的数据被查出来
        Date afterTime = DateUtil.offsetMinute(now,-(int)SecureInvokeService.RETRY_INTERVAL_MINUTES);
        return lambdaQuery().eq(SecureInvokeRecord::getStatus, SecureInvokeRecord.STATUS_WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, new Date()) // nextRetryTime < now
                .lt(SecureInvokeRecord::getCreateTime, afterTime) // createTime < afterTime
                .list();
    }
}
