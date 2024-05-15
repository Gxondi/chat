package com.hyh.mallchat.common.common.service;

import com.hyh.mallchat.common.common.exception.BusinessException;
import com.hyh.mallchat.common.common.exception.CommonErrorEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class LockService {

    @Autowired
    private RedissonClient redissonClient;
    @SneakyThrows
    public <T> T executeReissonLock(String key, Integer waitTime, TimeUnit unit, Supplier<T> supplier){
        RLock lock = redissonClient.getLock(key);
        boolean success = lock.tryLock(waitTime, unit);
        if (!success){
            throw new BusinessException(CommonErrorEnum.LOCK_LIMIT);
        }
        try {
            return supplier.get();//执行锁内的代码逻辑

        } finally {
            lock.unlock();
        }
    }
    public <T> T executeReissonLock(String key,  Supplier<T> supplier) throws InterruptedException {
       return executeReissonLock(key,-1,TimeUnit.DAYS,supplier);
    }
    public <T> T executeReissonLock(String key,  Runnable runnable) throws InterruptedException {
        return executeReissonLock(key,-1,TimeUnit.DAYS,()->{
            runnable.run();
            return null;
        });
    }

    @FunctionalInterface
    public interface Supplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
