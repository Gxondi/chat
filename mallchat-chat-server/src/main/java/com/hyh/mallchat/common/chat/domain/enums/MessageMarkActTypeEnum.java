package com.hyh.mallchat.common.chat.domain.enums;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum MessageMarkActTypeEnum {
    MARK(1,"确认标记"),
    UNMARK(2,"取消标记");

    private Integer type;
    private String desc;

    private static Map<Integer, MessageMarkActTypeEnum> cache = new HashMap<>();
    static {
        cache = Arrays.stream(MessageMarkActTypeEnum.values()).collect(Collectors.toMap(MessageMarkActTypeEnum::getType, Function.identity()));
    }

    public static MessageMarkActTypeEnum of(Integer actType) {
        return cache.get(actType);
    }
}
