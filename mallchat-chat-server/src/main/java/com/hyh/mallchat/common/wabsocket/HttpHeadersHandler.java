package com.hyh.mallchat.common.wabsocket;
import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import org.apache.commons.lang3.StringUtils;


import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * http升级websocket之前的一些了操作
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof HttpRequest){
            HttpRequest httpRequest  = (HttpRequest)msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(httpRequest.getUri().toString());
            Optional<String> tokenOptional = Optional
                    .ofNullable(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            if (tokenOptional.isPresent()) {
                NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, tokenOptional.get());
            }
            httpRequest.setUri(urlBuilder.getPath().toString());
            HttpHeaders headers = ((FullHttpRequest) httpRequest).headers();
            String ip = headers.get("X-Real-IP");
            if(StringUtils.isBlank(ip)){
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            //设置ip
            NettyUtil.setAttr(ctx.channel(),NettyUtil.IP,ip);
            ctx.pipeline().remove(this);
        }
        //继续往下执行
        ctx.fireChannelRead(msg);
    }
}
