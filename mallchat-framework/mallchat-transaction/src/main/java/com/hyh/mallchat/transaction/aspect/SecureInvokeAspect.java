package com.hyh.mallchat.transaction.aspect;

import cn.hutool.core.date.DateUtil;

import com.hyh.mallchat.transaction.annotation.SecureInvoke;
import com.hyh.mallchat.transaction.domain.dto.SecureInvokeDTO;
import com.hyh.mallchat.transaction.domain.entity.SecureInvokeRecord;
import com.hyh.mallchat.transaction.service.SecureInvokeHandler;
import com.hyh.mallchat.transaction.service.SecureInvokeService;
import com.hyh.mallchat.transaction.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Aspect
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 1) //确保最先执行
@Component
public class SecureInvokeAspect {

    @Autowired
    private SecureInvokeService secureInvokeService;
    @Around("@annotation(secureInvoke)")
    public Object around( ProceedingJoinPoint joinPoint,SecureInvoke secureInvoke) throws Throwable {
        //是否异步
        boolean async = secureInvoke.async();
        //是否在事务中
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();
        if(SecureInvokeHandler.isInvoking() || !inTransaction){
            return joinPoint.proceed();
        }
        //获取方法，参数，类名等信息
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<String> parameters = Stream.of(method.getParameterTypes()).map(Class::getName).collect(Collectors.toList());
        //构建DTO
        SecureInvokeDTO build = SecureInvokeDTO.builder()
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(JsonUtils.toStr(parameters))
                .args(JsonUtils.toStr(joinPoint.getArgs()))
                .build();
        //构建记录
        SecureInvokeRecord record = SecureInvokeRecord.builder()
                .secureInvokeDTO(build)
                .maxRetryTimes(secureInvoke.maxRetryTimes())
                .nextRetryTime(DateUtil.offsetMinute(new Date(), (int) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .build();
        secureInvokeService.invoke(async, record);
        return null;
    }

}
