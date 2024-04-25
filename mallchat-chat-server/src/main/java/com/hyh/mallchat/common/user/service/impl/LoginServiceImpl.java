package com.hyh.mallchat.common.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.utils.JwtUtils;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import com.hyh.mallchat.common.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {
    public static final int TOKEN_EXPIRE_DAYS = 30;
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public String login(Long uid) {
        String token = jwtUtils.createToken(uid);
        RedisUtils.set(getUserTokenKey(uid), token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        return token;
    }

    /**
     * 获取有效的uid
     * @param token
     * @return
     */
    @Override
    public Long getValidUid(String token) {
        //解析token
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjcsImNyZWF0ZVRpbWUiOjE3MTMwMTkwMzB9.QoaSHsTxC3fn_Y8NqUuRaPpfT9yzOgx9mG784RlY-6w
        Long uid = jwtUtils.getUidOrNull(token);
        if(Objects.isNull(uid)){
            return null;
        }
        //校验token是否有效
        String oldToken = RedisUtils.getStr(getUserTokenKey(uid));
        if(StringUtils.isBlank(oldToken)){
            return null;
        }
        return Objects.equals(token,oldToken)?uid:null;
    }
    /**
     * 刷新token
     * @param token
     */
    @Override
    public void renewalTokenIfNecessary(String token) {
        Long uid = getValidUid(token);
        String key = getUserTokenKey(uid);
        Long expire = RedisUtils.getExpire(key, TimeUnit.DAYS);
        if(expire == -2){
            return;
        }else {
            RedisUtils.expire(getUserTokenKey(uid), TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
        }
    }

    /**
     * 获取用户token key
     * @param uid
     * @return
     */
    public String getUserTokenKey(Long uid) {
        String key = RedisKey.getKey(RedisKey.USER_KEY_STRING, uid);
       return key;
    }



}
