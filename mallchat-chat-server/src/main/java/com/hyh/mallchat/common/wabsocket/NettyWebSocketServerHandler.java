package com.hyh.mallchat.common.wabsocket;

import cn.hutool.json.JSONUtil;
import com.hyh.mallchat.common.domain.vo.rep.WSBaseReq;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandler.Sharable;
import com.hyh.mallchat.common.domain.enums.WSReqTypeEnum;

@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete){
            System.out.println("握手成功");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲超时，关闭连接");
                //TODO 用户下线
            }
        }


    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text = textWebSocketFrame.text();
        System.out.println("接收到的消息：" + text);

        WSBaseReq wsMessage = JSONUtil.toBean(text, WSBaseReq.class);
        switch (WSReqTypeEnum.of(wsMessage.getType())){
            case LOGIN:
                // 业务逻辑处理
                System.out.println("登录");
                ctx.writeAndFlush(new TextWebSocketFrame("登录成功"));
                break;
            case HEARTBEAT:
                // 业务逻辑处理
                break;
            case AUTHORIZE:
                break;
        }
    }

}
