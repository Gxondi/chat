package com.hyh.mallchat.common.chat.domain.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum RoomFriendEnum {
    YES(1, "热点"),
    NO(0, "非热点");
    private Integer type;
    private String desc;
    RoomFriendEnum(Integer type, String desc){
        this.type = type;
        this.desc = desc;
    }
    private static Map<Integer, RoomFriendEnum> cache = new HashMap<>();
    static{
        cache = Arrays.stream(RoomFriendEnum.values()).collect(Collectors.toMap(RoomFriendEnum::getType, Function.identity()));
    }
    public static RoomFriendEnum of(Integer type){
        return cache.get(type);
    }
}
