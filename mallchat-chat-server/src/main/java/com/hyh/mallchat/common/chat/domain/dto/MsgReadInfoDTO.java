package com.hyh.mallchat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MsgReadInfoDTO {
    @ApiModelProperty("消息id")
    private Long msgId;
    @ApiModelProperty("已读")
    private Integer readCount;
    @ApiModelProperty("未读")
    private Integer UnReadCount;
}
