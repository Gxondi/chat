package com.hyh.mallchat.common.common.thread;

import lombok.AllArgsConstructor;

import java.util.concurrent.ThreadFactory;
@AllArgsConstructor
public class MyThreadFactory implements ThreadFactory {
    private static final MyUncaughtExceptionHandler MY_UNCAUGHT_EXCEPTION_HANDLER = new MyUncaughtExceptionHandler();
    private ThreadFactory original;
    @Override
    public Thread newThread(Runnable r) {
        Thread thread = original.newThread(r);
        thread.setUncaughtExceptionHandler(MY_UNCAUGHT_EXCEPTION_HANDLER);
        return thread;
    }
}
