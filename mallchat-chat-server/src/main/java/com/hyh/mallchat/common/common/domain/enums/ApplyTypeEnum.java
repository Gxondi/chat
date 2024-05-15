package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyTypeEnum {
    APPLY_FRIEND(1,"加好友");
    private Integer type;
    private String desc;
}
