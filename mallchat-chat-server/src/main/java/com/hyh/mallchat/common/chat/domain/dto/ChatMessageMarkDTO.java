package com.hyh.mallchat.common.chat.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class ChatMessageMarkDTO {
    @ApiModelProperty("操作者")
    private Long uid;

    @ApiModelProperty("消息id")
    private Long msgId;

    @ApiModelProperty("标记类型 1点赞 2举报")
    private Integer markType;

    @ApiModelProperty("动作类型 1确认 2取消")
    private Integer actType;
}
