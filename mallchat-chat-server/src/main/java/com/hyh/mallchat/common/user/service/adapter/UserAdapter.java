package com.hyh.mallchat.common.user.service.adapter;

import com.hyh.mallchat.common.user.domain.entity.User;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

public class UserAdapter {
    public static User buildUserSave(String openId) {
        User build = User.builder().openId(openId).build();
        return build;
    }
    public static User buildAuthorizeUser(WxOAuth2UserInfo userInfo) {
       return User.builder().name(userInfo.getNickname()).avatar(userInfo.getHeadImgUrl()).build();
    }
}
