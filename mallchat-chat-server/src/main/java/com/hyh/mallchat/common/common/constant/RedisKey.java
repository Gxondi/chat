package com.hyh.mallchat.common.common.constant;

public class RedisKey {
    /**
     * 用户token存放
     */
    public static final String USER_TOKEN_STRING = "userToken:uid_%d";
    public static final String BASE_KEY= "mallchat:chat:";
    public static final String USER_KEY_STRING= "userToken:uid_%d";
    public static final String USER_MODIFY_STRING= "userModify:uid_%d";
    public static final String USER_INFO_STRING= "userInfo:uid_%d";

    /**
     * 用户的信息汇总
     */
    public static final String USER_SUMMARY_STRING = "userSummary:uid_%d";
    /**
     * 房间详情
     */
    public static final String ROOM_INFO_STRING = "roomInfo:roomId_%d";
    /**
     * 群组详情
     */
    public static final String GROUP_INFO_STRING = "groupInfo:roomId_%d";
    /**
     * 热门房间列表
     */
    public static final String HOT_ROOM_ZET = "hotRoom";

    /**
     * 在线用户列表
     */
    public static final String ONLINE_UID_ZET = "online";
    public static final String OFFLINE_UID_ZET = "offline";
    public static String getKey(String key,Object... args){
        return BASE_KEY + String.format(key,args);
    }
}
