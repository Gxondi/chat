package com.hyh.mallchat.common.wabsocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.LoginService;
import com.hyh.mallchat.common.user.service.WxMsgService;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.common.wabsocket.domain.dto.WSChannelExtraDTO;
import com.hyh.mallchat.common.wabsocket.domain.vo.req.WSBaseReq;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;

import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    /**
     * 保存用户channel（临时/登录）
     */
    private final static ConcurrentMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 临时保存code和channel的映射关系
     * 用户一直扫码，会导致oom，使用本地缓存
     */
    Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder().maximumSize(MAXIMUM_SIZE).expireAfterWrite(DURATION).build();


    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginService loginService;

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }
    /**
     * 处理登录请求
     * @param channel
     */
    @SneakyThrows
    @Override
    public void handlerLoginReq(Channel channel) {
        //生成随机不重复的登录码
        Integer code = generateLoginCode(channel);
        WxMpQrCodeTicket wxMpQrCodeTicket;
        try {
            //获取临时二维码
            wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
        //给前端发送二维码
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        //TODO 用户下线
    }

    /**
     * 扫码登录成功
     *
     * @param code
     * @param uid
     */
    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        //调用登录成功的业务逻辑
        User user = userDao.getById(uid);
        WAIT_LOGIN_MAP.invalidate(code);
        //获取token
        String token = loginService.login(uid);
        //给前端发送登录成功的消息
        loginSuccess(channel, user, token);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        /**
         * 1.给前端发送等待授权的消息
         * buildWaitAuthorize构造返回给前端的消息体
         *
         */
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorize());
    }

    /**
     * 授权
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            //token有效逻辑
            User user = userDao.getById(validUid);
            loginSuccess(channel,user,token);
        } else {
            //token无效逻辑
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    private void loginSuccess(Channel channel, User user, String token) {
        /**
         * 1.给前端发送登录成功的消息
         * buildResp构造返回给前端的消息体
         */
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        sendMsg(channel, WebSocketAdapter.buildResp(user, token));
    }


    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        //推送消息给前端
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        int code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);

        } while (!Objects.isNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        /**
         * WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel))
         * 返回值
         * 如果所指定的 key 已经在 HashMap 中存在，返回和这个 key 值对应的 value, 如果所指定的 key 不在 HashMap 中存在，则返回 null。
         * 注意：如果指定 key 之前已经和一个 null 值相关联了 ，则该方法也返回 null。
         */
        //将code和channel的映射关系保存到本地缓存
        WAIT_LOGIN_MAP.put(code, channel);
        return code;
    }

}
