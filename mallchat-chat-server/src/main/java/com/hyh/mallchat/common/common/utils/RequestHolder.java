package com.hyh.mallchat.common.common.utils;

import com.hyh.mallchat.common.common.domain.dto.RequestInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestHolder {

    private static final ThreadLocal<RequestInfo> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setRequestInfo(RequestInfo requestInfo) {
        log.info("RequestHolder setRequestInfo");
        USER_ID_HOLDER.set(requestInfo);
    }

    public static RequestInfo getRequestInfo() {
        return USER_ID_HOLDER.get();
    }

    public static void remove() {
        USER_ID_HOLDER.remove();
    }
}
