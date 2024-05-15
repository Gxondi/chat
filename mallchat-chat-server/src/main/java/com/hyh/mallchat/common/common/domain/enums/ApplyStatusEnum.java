package com.hyh.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApplyStatusEnum {
    WAIT_APPROVE(1,"等带审批"),
    APPROVED(2,"已通过"),;
    private Integer type;
    private String desc;
}
