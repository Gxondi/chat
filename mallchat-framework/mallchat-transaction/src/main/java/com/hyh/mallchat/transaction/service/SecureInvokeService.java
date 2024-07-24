package com.hyh.mallchat.transaction.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hyh.mallchat.transaction.dao.SecureInvokeRecordDao;
import com.hyh.mallchat.transaction.domain.dto.SecureInvokeDTO;
import com.hyh.mallchat.transaction.domain.entity.SecureInvokeRecord;
import com.hyh.mallchat.transaction.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
@Slf4j
public class SecureInvokeService {
    public static final double RETRY_INTERVAL_MINUTES = 2D;

    private SecureInvokeRecordDao secureInvokeRecordDao;

    private Executor executor;
    @Scheduled(cron = "*/5 * * * * ?")
    public void retry(){
        List<SecureInvokeRecord> waitRetryRecords = secureInvokeRecordDao.getWaitRetryRecords();
        for (SecureInvokeRecord waitRetryRecord : waitRetryRecords) {
            doAsyncInvoke(waitRetryRecord);
        }
    }
    public void invoke(boolean async, SecureInvokeRecord record) {
        //是否在事务中
        boolean inTransaction = TransactionSynchronizationManager.isActualTransactionActive();

        //非事务状态，直接执行，不做任何保证。
        if (!inTransaction) {
            return;
        }
        sava(record);
        /**
         * 事务提交后立刻执行，不要等着定时任务执行，因为定时任务可能会延迟
         */
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @SneakyThrows
            @Override
            public void afterCommit() {
                //事务后执行
                if (async) {
                    // 异步执行
                    doAsyncInvoke(record);
                } else {
                    // 同步执行
                    doSyncInvoke(record);
                }
            }

        });

    }

    private void sava(SecureInvokeRecord record) {
        secureInvokeRecordDao.save(record);
    }

    /**
     * 异步执行
     */
    private void doAsyncInvoke(SecureInvokeRecord record) {
        executor.execute(()->{
            System.out.println(Thread.currentThread().getName());
            doSyncInvoke(record);
        });

    }

    private void retryRecord(SecureInvokeRecord record, String errorMsg) {
        Integer retryTimes = record.getRetryTimes() + 1;
        SecureInvokeRecord updateRecord = new SecureInvokeRecord();
        updateRecord.setId(record.getId());
        updateRecord.setFailReason(errorMsg);
        updateRecord.setNextRetryTime(getNextRetryTime(retryTimes));
        if(retryTimes>= record.getMaxRetryTimes()){
            updateRecord.setStatus(SecureInvokeRecord.STATUS_FAIL);
        }else {
            updateRecord.setRetryTimes(retryTimes);
        }
        secureInvokeRecordDao.updateById(updateRecord);
    }

    private Date getNextRetryTime(Integer retryTimes) {
        double pow = Math.pow(RETRY_INTERVAL_MINUTES, retryTimes);
        return new Date(System.currentTimeMillis() + (long) pow);
    }

    private void removeRecord(Long id) {
        secureInvokeRecordDao.removeById(id);
    }

    /**
     * 同步执行
     */
    private void doSyncInvoke(SecureInvokeRecord record) {
        SecureInvokeDTO secureInvokeDTO = record.getSecureInvokeDTO();
        try {
            SecureInvokeHandler.isInvoking();
            // 通过反射获取类
            Class<?> beanClass = Class.forName(secureInvokeDTO.getClassName());
            Object bean = SpringUtil.getBean(beanClass);
            List<String> parameterStrings = JsonUtils.toList(secureInvokeDTO.getParameterTypes(), String.class);
            //获取参数类型 String.class
            List<Class<?>> parameterClasses = getParameters(parameterStrings);
            // 通过bean名，方法名，以及参数类型 获取方法
            Method method = ReflectUtil.getMethod(beanClass, secureInvokeDTO.getMethodName(), parameterClasses.toArray(new Class[]{}));
            Object[] args = getArgs(secureInvokeDTO, parameterClasses);
            // 执行方法
            method.invoke(bean, args);
            removeRecord(record.getId());
        } catch (Throwable e) {
            log.error("SecureInvokeService invoke fail", e);
            retryRecord(record, e.getMessage());
        }finally {

            SecureInvokeHandler.invoked();
        }

    }

    @NonNull
    private List<Class<?>> getParameters(List<String> parameterStrings) {
        return parameterStrings.stream()
                .map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        log.error("SecureInvokeService getParameters fail", e);
                    }
                    return null;
                }).collect(Collectors.toList());
    }

    @NotNull
    private Object[] getArgs(SecureInvokeDTO secureInvokeDTO, List<Class<?>> parameterClasses) {
        JsonNode jsonNode = JsonUtils.toJsonNode(secureInvokeDTO.getArgs());
        Object[] args = new Object[jsonNode.size()];
        for (int i = 0; i < jsonNode.size(); i++) {
            Class<?> aClass = parameterClasses.get(i);
            args[i] = JsonUtils.nodeToValue(jsonNode.get(i), aClass);
        }
        return args;
    }
}
