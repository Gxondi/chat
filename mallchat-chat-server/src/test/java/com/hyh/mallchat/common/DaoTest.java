package com.hyh.mallchat.common;

import com.hyh.mallchat.common.common.domain.enums.IdempotentEnum;
import com.hyh.mallchat.common.common.domain.enums.ItemEnum;
import com.hyh.mallchat.common.common.thread.MyUncaughtExceptionHandler;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.ItemConfig;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.domain.entity.UserBackpack;
import com.hyh.mallchat.common.user.service.IUserBackpackService;
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
    public static final long UID = 10028L;
    @Autowired
    private UserDao userDao;



    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoginService loginService;
    @Autowired
    private IUserBackpackService iUserBackpackService;
    @Test
    public void test(){
        String login = loginService.login(10028L);
        //Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjEwMDI4LCJjcmVhdGVUaW1lIjoxNzEzNTA4MDU3fQ.VNrzOm4Km11cQ-GRRAY8e0rc_NG61FdlXUeEIyADc3w
        System.out.println(login);
    }
    @Test
    public void acquire(){
        iUserBackpackService.acquireItem(UID, ItemEnum.PLANET.getId(), IdempotentEnum.UID,UID+"");
    }
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
