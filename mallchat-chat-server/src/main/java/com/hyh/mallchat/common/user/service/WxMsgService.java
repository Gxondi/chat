package com.hyh.mallchat.common.user.service;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

public interface WxMsgService {
    /**
     * 扫码事件处理
     * @param wxMpXmlMessage
     * @return
     */
    WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage);
    /**
     * 授权
     * @param userInfo
     */
    void authorize(WxOAuth2UserInfo userInfo);
}
