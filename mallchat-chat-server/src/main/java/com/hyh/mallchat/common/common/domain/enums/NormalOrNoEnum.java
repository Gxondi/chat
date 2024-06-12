package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum NormalOrNoEnum {
    NORMAL(0,"正常"),
    NO(1,"禁用"),;
    private Integer status;
    private String desc;

    private static Map<Integer, NormalOrNoEnum> cache;
    static {
        cache = Arrays.stream(NormalOrNoEnum.values()).collect(Collectors.toMap(NormalOrNoEnum::getStatus, Function.identity()));
    }

    public static NormalOrNoEnum of(Integer type) {
        return cache.get(type);
    }

    public static Integer toStatus(Boolean bool) {
        return bool ? NORMAL.getStatus() : NO.getStatus();
    }
}
