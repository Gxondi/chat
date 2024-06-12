package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RoomHotFlagEnum {
    YES(1,"热点群"),
    NO(0,"非热点"),;
    private Integer type;
    private String desc;
    private static Map<Integer, RoomHotFlagEnum> cache = new HashMap<>();
    static{
        cache = Arrays.stream(RoomHotFlagEnum.values()).collect(Collectors.toMap(RoomHotFlagEnum::getType, Function.identity()));
    }
    public static RoomHotFlagEnum of(Integer type){
        return cache.get(type);
    }
}
