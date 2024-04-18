package com.hyh.mallchat.common.user.service;

public interface LoginService {
    String login(Long uid);
    Long getValidUid(String token);
    void renewalTokenIfNecessary(String token);
}
