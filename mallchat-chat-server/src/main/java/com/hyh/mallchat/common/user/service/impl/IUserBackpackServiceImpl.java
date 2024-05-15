package com.hyh.mallchat.common.user.service.impl;

import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.domain.enums.IdempotentEnum;
import com.hyh.mallchat.common.common.domain.enums.YesOrNoEnum;
import com.hyh.mallchat.common.common.service.LockService;
import com.hyh.mallchat.common.user.dao.UserBackpackDao;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author CondiX
 * @since 2024-04-18
 */

@Service
public class IUserBackpackServiceImpl implements IUserBackpackService {
    @Autowired
    private UserBackpackDao userBackpackDao;

    @Autowired
    private LockService lockService;
    @Autowired
    @Lazy
    private IUserBackpackServiceImpl iUserBackpackService;

    @Override
    @Transactional
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //获取幂等号=itemId+source+businessId
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        iUserBackpackService.doAcquireItem(uid, itemId, idempotent);
//        RLock lock = redissonClient.getLock("idempotent" + idempotent);
//        boolean b = lock.tryLock();
//        AssertUtil.isTrue(b, "请求太频繁");
//        try {
//            UserBackpack backpack = userBackpackDao.getIdempotent(idempotent);
//            if (Objects.nonNull(backpack)) {
//                return;
//            }
//            //发放物品
//            UserBackpack insert = UserBackpack.builder()
//                    .uid(uid)
//                    .itemId(itemId)
//                    .idempotent(idempotent)
//                    .status(YesOrNoEnum.NO.getStatus())
//                    .build();
//            userBackpackDao.save(insert);
//        } finally {
//            lock.unlock();
//        }
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        //上锁，进行串行处理
        UserBackpack backpack = userBackpackDao.getIdempotent(idempotent);
        if (Objects.nonNull(backpack)) {
            return;
        }
        //发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .idempotent(idempotent)
                .status(YesOrNoEnum.NO.getStatus())
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d,%d,%s", itemId, idempotentEnum.getType(), businessId);
    }


}
