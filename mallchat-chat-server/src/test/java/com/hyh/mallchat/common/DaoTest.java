package com.hyh.mallchat.common;

import com.hyh.mallchat.common.common.thread.MyUncaughtExceptionHandler;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    @Autowired
    private UserDao userDao;

    @Test
    public void test(){
        User byId = userDao.getById(1);
        System.out.println(byId);
    }
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoginService loginService;
    @Test
    public void redis() {
       String s= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjcsImNyZWF0ZVRpbWUiOjE3MTMwMTk5NjZ9.1tDKkwj1gOWTPDEBUdYbcENguoI7y3Ouh3NUxmI0N8M";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);

    }
    @Autowired
    private RedissonClient redissonClient;
    @Test
    public void redission() {
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println("加锁成功");
        lock.unlock();
    }
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Test
    public void thread() throws InterruptedException {
        threadPoolTaskExecutor.execute(()->{
            log.error("测试异常");
            throw new RuntimeException("1234");
        });
        Thread.sleep(200);
    }


}
