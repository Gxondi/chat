package com.hyh.mallchat.transaction.service;

import java.util.Objects;

public class SecureInvokeHandler {

    private static final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    public static boolean isInvoking() {
        return Objects.nonNull(threadLocal.get());
    }
    public static void setInvoke() {
        threadLocal.set(Boolean.TRUE);
    }


    public static void invoked() {
        threadLocal.remove();
    }
}
