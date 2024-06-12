package com.hyh.mallchat.common.wabsocket;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import io.netty.channel.Channel;

public class NettyUtil {
    public static final AttributeKey<String> TOKEN =AttributeKey.valueOf("token");
    public static final AttributeKey<String> IP =AttributeKey.valueOf("ip");
    public static final AttributeKey<Long> UID =AttributeKey.valueOf("uid");
    public static <T> void setAttr(Channel channel, AttributeKey<T> key, T value) {
        Attribute<T> attr = channel.attr(key);
        attr.set(value);
    }
    public static <T> T getAttr(Channel channel, AttributeKey<T> key) {
        Attribute<T> attr = channel.attr(key);
        return attr.get();
    }
}
