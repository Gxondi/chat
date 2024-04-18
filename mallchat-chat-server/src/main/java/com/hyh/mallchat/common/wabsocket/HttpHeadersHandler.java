package com.hyh.mallchat.common.wabsocket;
import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;


import java.util.Optional;

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
        }
        ctx.fireChannelRead(msg);

    }
}
