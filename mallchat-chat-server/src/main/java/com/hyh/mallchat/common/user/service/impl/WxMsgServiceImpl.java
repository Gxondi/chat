package com.hyh.mallchat.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.UserService;
import com.hyh.mallchat.common.user.service.WxMsgService;
import com.hyh.mallchat.common.user.service.adapter.TextBuilder;
import com.hyh.mallchat.common.user.service.adapter.UserAdapter;
import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class WxMsgServiceImpl implements WxMsgService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Autowired
    private WebSocketService webSocketService;
    private static final ConcurrentMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();
    @Value("${wx.mp.callback}")
    private String callback;
    public static final String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

    /**
     * 用户扫码成功事件处理
     * 一张携带参数的二维码。当用户扫码后关注公众号。公众号会给我们后台回调一个关注事件 OR 扫码事件
     * 扫码后判断用户是否注册，未注册则注册，注册后等待用户授权
     * @param wxMpXmlMessage
     * @return
     */
    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        //扫码后微信回调，获取用户openId
        String openId = wxMpXmlMessage.getFromUser();
        //获取事件code
        Integer code = this.getEventKey(wxMpXmlMessage);
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }
        //用户是否注册
        User user = userDao.getUserByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && Objects.nonNull(user.getAvatar());
        //用户已注册且已授权
        if (authorized && registered) {
            //登录成功后的业务处理 用过code找到channel，判断在连接上
            webSocketService.scanLoginSuccess(code, user.getId());
            return null;
        }
        //用户未注册
        if (!registered) {
            //注册用户往数据库内添加数据
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }
        //等待用户授权过程中临时保存openId和code的映射关系
        WAIT_AUTHORIZE_MAP.put(openId, code);
        //等待用户授权
        webSocketService.waitAuthorize(code);
        //用户授权后的回调地址
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return new TextBuilder().build("请点击登录: <a href = \"" + authorizeUrl + "\">登录 </a>", wxMpXmlMessage);
    }

    /**
     * 用户授权成功事件处理 在callback中调用
     * 更新临时用户的信息
     * @param userInfo
     */
    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getUserByOpenId(openid);
        //更新用户
        if (StrUtil.isBlank(user.getAvatar())) {
            fillUserInfo(user.getId(), userInfo);
        }
        //更新后删除临时映射关系
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);
        //登录成功后的业务处理 用过code找到channel
        webSocketService.scanLoginSuccess(code, user.getId());
    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(userInfo);
        user.setId(uid);
        user.setName(userInfo.getNickname());
        userDao.updateById(user);
    }
    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            return Integer.parseInt(wxMpXmlMessage.getEventKey().replace("qrscene_", ""));
        } catch (NumberFormatException e) {
            log.error("扫码事件处理异常", e);
            return null;
        }
    }
}
