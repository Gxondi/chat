package com.hyh.mallchat.common.wabsocket.service;

import com.hyh.mallchat.common.wabsocket.domain.vo.req.WSBaseReq;
import io.netty.channel.Channel;

public interface WebSocketService {

    void connect(Channel channel);

    void handlerLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String token);
}
