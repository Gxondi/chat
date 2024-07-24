package com.hyh.mallchat.common.wabsocket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hyh.mallchat.common.common.constant.RedisKey;
import com.hyh.mallchat.common.common.domain.enums.RoleEnum;
import com.hyh.mallchat.common.common.event.UserOfflineEvent;
import com.hyh.mallchat.common.common.event.UserOnlineEvent;
import com.hyh.mallchat.common.common.utils.RedisUtils;
import com.hyh.mallchat.common.user.dao.UserDao;
import com.hyh.mallchat.common.user.domain.entity.User;
import com.hyh.mallchat.common.user.service.IRoleService;
import com.hyh.mallchat.common.user.service.LoginService;
import com.hyh.mallchat.common.user.service.cache.UserCache;
import com.hyh.mallchat.common.wabsocket.NettyUtil;
import com.hyh.mallchat.common.wabsocket.adapter.WebSocketAdapter;
import com.hyh.mallchat.common.wabsocket.domain.dto.WSChannelExtraDTO;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;

import com.hyh.mallchat.common.wabsocket.service.WebSocketService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.nio.file.OpenOption;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {

    public static final int MAXIMUM_SIZE = 1000;
    public static final Duration DURATION = Duration.ofHours(1);
    /**
     * 管理所有用户连接，登录/未登录
     */
    private final static ConcurrentMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();
    /**
     * 临时保存code和channel的映射关系
     * 用户一直扫码，会导致oom，使用本地缓存
     *
     */
    Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder().maximumSize(MAXIMUM_SIZE).expireAfterWrite(DURATION).build();

    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();


    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserCache userCache;

    @Autowired
    private LoginService loginService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    /**
     * 处理登录请求
     *
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
        WSChannelExtraDTO remove = ONLINE_WS_MAP.remove(channel);
        //TODO 用户下线
        Optional<Long> uidOptional = Optional.ofNullable(remove).map(WSChannelExtraDTO::getUid);
        boolean offline = offline(channel, uidOptional);
        if (offline && uidOptional.isPresent()) {
            //用户下线
            User user = new User();
            user.setId(uidOptional.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }

    }

    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove(channel);
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(c -> c.equals(channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;

    }

    private Integer generateLoginCode(Channel channel) {
        int code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);

        } while (!Objects.isNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));
        System.out.println("code:" + code);
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
        //删除code
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
     *
     * @param channel
     * @param token
     */
    @Override
    public void authorize(Channel channel, String token) {
        Long validUid = loginService.getValidUid(token);
        if (Objects.nonNull(validUid)) {
            //token有效逻辑
            User user = userDao.getById(validUid);
            loginSuccess(channel, user, token);
        } else {
            //token无效逻辑
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }


    @Override
    public void sendMsgToAll(WSBaseResp<?> msg) {

    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (Objects.isNull(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> {
                sendMsg(channel, wsBaseMsg);
            });
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {

        sendToAllOnline(wsBaseResp, null);
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseMsg, Long skipUid) {
        ONLINE_WS_MAP.forEach((channel, ext) -> {
            if (Objects.nonNull(skipUid) && Objects.equals(ext.getUid(), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseMsg));
        });

    }

    private void loginSuccess(Channel channel, User user, String token) {
        /**
         * 1.给前端发送登录成功的消息
         * buildResp构造返回给前端的消息体
         */
        online(channel, user.getId());
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());

        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER)));
        //发送用户上线事件
        boolean online = userCache.isOnline(user.getId());
        if (!online) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    private void online(Channel channel, Long uid) {
        getOrInitChannelExt(channel).setUid(uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        ONLINE_UID_MAP.get(uid).add(channel);
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
    }

    /**
     * 获取或初始化channel的扩展信息
     *
     * @param channel
     * @return
     */
    private WSChannelExtraDTO getOrInitChannelExt(Channel channel) {
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.getOrDefault(channel, new WSChannelExtraDTO());
        WSChannelExtraDTO old = ONLINE_WS_MAP.putIfAbsent(channel, wsChannelExtraDTO);
        return ObjectUtil.isNull(old) ? wsChannelExtraDTO : old;
    }


    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        //推送消息给前端
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }



}
