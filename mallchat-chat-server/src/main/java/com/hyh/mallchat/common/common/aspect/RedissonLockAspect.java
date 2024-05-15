package com.hyh.mallchat.common.common.aspect;

import cn.hutool.core.util.StrUtil;
import com.hyh.mallchat.common.common.annotation.RedissonLock;
import com.hyh.mallchat.common.common.service.LockService;
import com.hyh.mallchat.common.common.utils.SpElUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Slf4j
@Aspect
@Order(0)//确保比事务注解先执行，分布式锁在事务外
public class RedissonLockAspect {

    @Autowired
    private LockService lockService;
    /**
     * proceedingJoinPoint 连接点 执行点，与方法连接
     *
     * @return
     */
    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint, RedissonLock redissonLock) throws Throwable{
        //获取当前方法,转MethodSignature 方法签名(execution(ApiResult com.hyh.mallchat.common.user.controller.UserController.modifyName(ModifyNameReq)))
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        //class com.hyh.mallchat.common.user.controller.UserController#modifyName
        String prefixKey = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String Key  = SpElUtils.parseSpEl(method,proceedingJoinPoint.getArgs(),redissonLock.key());
        /**
         * 环绕通知=前置+目标方法执行+后置通知，proceed方法就是用于启动目标方法执行的，即被注解标记的方法
         *
         * point.proceed()方法的作用：获取方法的执行结果
         *
         * 执行此方法 execution(void com.hyh.mallchat.common.user.service.impl.UserServiceImpl.modifyName(Long,String))
         */
        return lockService.executeReissonLock(prefixKey+":"+Key, redissonLock.waitTime(),redissonLock.unit(),proceedingJoinPoint::proceed);
    }
}
