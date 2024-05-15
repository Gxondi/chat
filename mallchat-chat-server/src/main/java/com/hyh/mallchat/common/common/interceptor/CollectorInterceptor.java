package com.hyh.mallchat.common.common.interceptor;

import cn.hutool.extra.servlet.ServletUtil;
import com.hyh.mallchat.common.common.domain.dto.RequestInfo;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
@Slf4j
@Component
public class CollectorInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("CollectorInterceptor preHandle");
        Object attribute = request.getAttribute(TokenInterceptor.UID);
        Long uid = Optional.ofNullable(attribute).map(Object::toString).map(Long::parseLong).orElse(null);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.setUid(uid);
        //协议升级之前取得ip
        requestInfo.setIp(ServletUtil.getClientIP(request));
        RequestHolder.setRequestInfo(requestInfo);
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }
}
