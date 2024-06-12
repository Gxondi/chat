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
public enum RoomTypeEnum {
    FRIEND(1,"单聊"),
    GROUP(2,"群聊"),;
    private Integer type;
    private String desc;
    private static Map<Integer, RoomTypeEnum> cache = new HashMap<>();
    static {
        cache = Arrays.stream(RoomTypeEnum.values()).collect(Collectors.toMap(RoomTypeEnum::getType, Function.identity()));
    }
    public static RoomTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
