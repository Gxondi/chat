package com.hyh.mallchat.common.wabsocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.hyh.mallchat.common.wabsocket.domain.dto.WSChannelExtraDTO;
import com.hyh.mallchat.common.wabsocket.domain.vo.req.WSBaseReq;

import com.hyh.mallchat.common.wabsocket.service.WebSocketService;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandler.Sharable;
import com.hyh.mallchat.common.wabsocket.domain.enums.WSReqTypeEnum;


@Slf4j
@Sharable
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 监听管道
     *
     * @param ctx
     * @throws Exception
     */
    private WebSocketService webSocketService;

    /**
     * 通道就绪事件 channelActive
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /**
         * 从ioc容器中根据上下文获取bean
         */
        webSocketService = SpringUtil.getBean(WebSocketService.class);
        webSocketService.connect(ctx.channel());
    }
    /**
     * userEventTriggered方法允许用户处理自定义的事件，
     * 这些事件可以是任何事情，不仅仅局限于网络操作。
     * 例如，可以使用它来触发心跳超时事件、连接空闲事件或者任何自定义的事件。
     * 当这些事件发生时，Netty的事件循环会调用这个方法，允许开发者在其内部实现具体的业务逻辑。
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //ServerHandshakeStateEvent-》服务器握手状态事件 HANDSHAKE_COMPLETE-》握手完成
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            System.out.println("握手请求");
            String token = NettyUtil.getAttr(ctx.channel(), NettyUtil.TOKEN);
            if (StrUtil.isNotBlank(token)) {
                this.webSocketService.authorize(ctx.channel(), token);
            }
        }
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            System.out.println("握手成功");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("读空闲超时，关闭连接");
                //TODO 用户下线
                userOffline(ctx.channel());
            }
        }


    }

    /**
     * 用户下线统一处理
     *
     * @param channel
     */
    private void userOffline(Channel channel) {
        webSocketService.remove(channel);
        channel.close();
    }

    /**
     * 通道读取事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        String text = msg.text();
        System.out.println("接收到的消息：" + text);

        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case AUTHORIZE:
                // 授权业务逻辑处理
                break;
            case HEARTBEAT:
                // 业务逻辑处理
                break;
            case LOGIN:
                this.webSocketService.handlerLoginReq(ctx.channel());
        }
    }

}
