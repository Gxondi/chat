package com.hyh.mallchat.common.common.interceptor;

import com.hyh.mallchat.common.common.exception.HttpErrorEnum;
import com.hyh.mallchat.common.user.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String UID = "uid";

    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("TokenInterceptor preHandle");
        String token = getToken(request);
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
           //登录状态
            request.setAttribute(UID, validUid);
        }
        else {//用户未登录
            boolean isPublicURI = isPublicURI(request);
            if (!isPublicURI) {
                HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            }
            return false;
        }
        return true;
    }

    private static boolean isPublicURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String[] split = uri.split("/");
        boolean isPublicURI = split.length>3 && "public".equals(split[3]);
        return isPublicURI;
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        return Optional.ofNullable(token).map(h -> h.replace(AUTHORIZATION, "")).orElse(null);
    }
}
