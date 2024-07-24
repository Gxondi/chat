package com.hyh.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendUnReadCountResp {
    @ApiModelProperty("未读消息数")
    private Integer unReadCount;
}
