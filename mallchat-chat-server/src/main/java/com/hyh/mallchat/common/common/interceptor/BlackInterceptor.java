package com.hyh.mallchat.common.common.interceptor;

import com.hyh.mallchat.common.common.domain.enums.BlackEnum;
import com.hyh.mallchat.common.common.exception.HttpErrorEnum;
import com.hyh.mallchat.common.common.utils.RequestHolder;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Component
public class BlackInterceptor implements HandlerInterceptor {
    @Autowired
    private UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("BlackInterceptor preHandle");
        HashMap<Integer, Set<String>> blackMap = userCache.getBlackMap();
        if(isBlackList(RequestHolder.getRequestInfo().getUid(), blackMap.get(BlackEnum.UID.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        if(isBlackList(RequestHolder.getRequestInfo().getIp(), blackMap.get(BlackEnum.IP.getType()))){
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        return true;
    }

    private boolean isBlackList(Object target, Set<String> set) {
        if(Objects.isNull(target) || CollectionUtils.isEmpty(set)){
            return false;
        }
        return set.contains(target.toString());
    }
}
