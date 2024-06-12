package com.hyh.mallchat.common.wabsocket.service;

import com.hyh.mallchat.common.wabsocket.domain.vo.req.WSBaseReq;
import com.hyh.mallchat.common.wabsocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

public interface WebSocketService {

    void connect(Channel channel);

    void handlerLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);


    void authorize(Channel channel, String token);

    void sendMsgToAll(WSBaseResp<?> msg);

    void sendToUid(WSBaseResp<?> wsBaseMsg, Long uid);

    void sendToAllOnline(WSBaseResp<?> wsBaseResp);

    void sendToAllOnline(WSBaseResp<?> wsBaseMsg, Long skipUid);
}
