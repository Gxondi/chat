package com.hyh.mallchat.transaction.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 保证方法成功执行。如果在事务内的方法，会将操作记录入库，保证执行。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SecureInvoke {
    int maxRetryTimes() default 3;

    boolean async() default true;
}
