package com.hyh.mallchat.common.common.constant;

public class RedisKey {
    public static final String BASE_KEY= "mallchat:chat:";
    public static final String USER_KEY_STRING= "userToken:uid_%d";
    public static String getKey(String key,Object... args){
        return BASE_KEY + String.format(key,args);
    }
}
