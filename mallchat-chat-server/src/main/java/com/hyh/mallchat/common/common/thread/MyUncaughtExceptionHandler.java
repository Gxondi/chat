package com.hyh.mallchat.common.common.thread;

import lombok.extern.slf4j.Slf4j;
/**
 * 自定义异常处理器
 */
@Slf4j
public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Exception in thread ",t.getName(),e);
    }
}
